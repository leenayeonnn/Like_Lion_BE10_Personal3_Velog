const seriesId = $('#series-id').val();
const blogUsername = $('#blog-username').val();
let postPage = 1;

$(document).ready(function () {
    loadPosts();
});

function loadPosts() {
    const postContainer = $('#post-container');

    $.ajax({
        url: '/api/posts/' + blogUsername + '/series/' + seriesId + '?page=' + postPage,
        type: 'GET',
        success: function (response) {
            response.posts.forEach(function (post) {
                const formattedDate = moment(post.registrationDate).format('YYYY/MM/DD HH:mm');
                const processedContent = $('<div>').html(post.content).text();
                const truncatedContent = processedContent.length > 200 ? processedContent.substring(0, 200) + '...' : processedContent;

                const postHtml = `
                    <a href="/@${blogUsername}/${post.id}">
                            <div class="post" data-post-id="${post.id}">
                                ${post.mainImgUrl ? `
                                    <div class="post-main-img">
                                        <img src="${post.mainImgUrl}" alt=""/>
                                    </div>
                                ` : ''}                            
                                <div class="post-title">
                                    <h2>${post.title}</h2>
                                </div>
                                <div class="post-content">
                                    ${truncatedContent}
                                </div>
                                <div class="post-date">
                                    ${formattedDate}
                                </div>
                                ${!post.isPublic ? `
                                    <div class="post-no-public">
                                        비공개
                                    </div>
                                ` : ''}
                            </div>
                    </a>
                `;

                postContainer.append(postHtml);
            });

            postContainer.append('<button id="load-more-post-btn" class="btn btn-dark">더 불러오기</button>')
        },
        error: function (error) {
            postContainer.append('<div id="no-posts">게시물이 없습니다</div>');
        }
    });
}

$('#post-container').on('click', '#load-more-post-btn', function () {
    $(this).remove(); // 현재 버튼 제거
    postPage++;
    loadPosts();
});