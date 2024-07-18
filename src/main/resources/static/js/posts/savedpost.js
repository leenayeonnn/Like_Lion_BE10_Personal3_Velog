$(document).ready(function () {
    const username = $('#username').val();

    $.ajax({
        url: "/api/posts/" + username + "/saves",
        type: 'GET',
        success: function (response) {
            // 시리즈 데이터를 받아서 select 요소에 옵션으로 추가
            const postContainer = $('#postContainer')


            response.forEach(function (post) {
                const formattedDate = moment(post.registrationDate).format('YYYY/MM/DD HH:mm');
                const processedContent = $('<div>').html(post.content).text();
                const truncatedContent = processedContent.length > 200 ? processedContent.substring(0, 200) + '...' : processedContent;
                console.log(processedContent);

                const postHtml = `
                            <div class="post" data-post-id="${post.id}">
                                <div class="post-title">
                                    <h2>${post.title}</h2>
                                </div>
                                <div class="post-content">
                                    ${truncatedContent}
                                </div>
                                <div class="post-date">
                                    ${formattedDate}
                                </div>
                                <input type="button" class="editBtn" value="수정">
                                <input type="button" class="deleteBtn" value="삭제">
                            </div>
                        `;
                postContainer.append(postHtml);
            });
        },
        error: function (error) {
            console.error('에러발생:', error);
        }
    });

});

const postContainer = $('#postContainer');

postContainer.on('click', '.deleteBtn', function () {
    const postId = $(this).closest('.post').data('post-id');

    $.ajax({
        url: "/api/posts/" + postId,
        type: 'DELETE',
        success: function (response) {
            $(`.post[data-post-id="${postId}"]`).remove();
        },
        error: function (error) {
            console.error('에러발생:', error);
        }
    });
});

postContainer.on('click', '.editBtn', function () {
    const postId = $(this).closest('.post').data('post-id');
    window.location.href = '/post/write?postId=' + postId;
});