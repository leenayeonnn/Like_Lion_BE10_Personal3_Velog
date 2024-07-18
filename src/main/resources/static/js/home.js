let page = 1;

$(document).ready(function () {
    $('#recent-btn').addClass('active');
    loadPosts("recent");
});

function loadPosts(orderMethod) {
    const postListElement = $('#post-list');
    $('#load-more-post-btn').remove();
    $('#no-posts').remove();

    $.ajax({
        url: '/api/posts',
        type: 'GET',
        data: {
            'orderMethod': orderMethod,
            'page': page
        },
        success: function (response) {

            response.posts.forEach(function (post) {
                const postDetailUrl = '/@' + post.blog.user.username + '/' + post.id;
                const blogUrl = '/@' + post.blog.user.username;
                const truncatedTitle = post.title > 15 ? post.title.substring(0, 15) + '...' : post.title;
                const processedContent = $('<div>').html(post.content).text();
                const truncatedContent = processedContent.length > 30 ? processedContent.substring(0, 30) + '...' : processedContent;
                const writer = '작성자 : ' + post.blog.name;

                const html = `
                    <div class="col mb-5">
                        <div class="post h-100">
                            ${post.mainImgUrl ? `
                                <img class="post-img-top" src="${post.mainImgUrl}" alt="..."/>
                            ` : ``}
                            <div class="post-body p-4">
                                <a href=${postDetailUrl}
                                   class="text-decoration-none">
                                    <h5 class="fw-bolder">${truncatedTitle}</h5>
                                    <p>${truncatedContent}</p>
                                </a>
                            </div>
                            <div class="post-footer">
                                <a href=${blogUrl}>
                                    <p>${writer}</p>
                                </a>
                            </div>
                        </div>
                    </div>
                `
                postListElement.append(html);
            });
            postListElement.closest('.container').append('<button id="load-more-post-btn" class="btn btn-dark">더 불러오기</button>')
        },
        error: function (error) {
            postListElement.closest('.container').append('<div id="no-posts" class="center">게시물이 없습니다</div>');
        }
    });
}

// ======

$('#recent-btn').click(function () {
    if ($(this).hasClass('active')) {
        return;
    }

    $(this).addClass('active');
    $('#view-btn').removeClass('active');

    page = 1;
    $('#post-list').empty();

    loadPosts('recent');
})

$('#view-btn').click(function () {
    if ($(this).hasClass('active')) {
        return;
    }

    $(this).addClass('active');
    $('#recent-btn').removeClass('active');

    page = 1;
    $('#post-list').empty();

    loadPosts('view');
})

//=====
$(document).on('click', '#load-more-post-btn', function () {
    page++;
    const orderMethod = $('#recent-btn').hasClass('active') ? 'recent' : 'view';
    loadPosts(orderMethod);
});