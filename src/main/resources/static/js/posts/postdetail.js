$('#writer-btn').click(function () {
    const currentUrl = window.location.href;
    const parts = currentUrl.split('/');

    let username = '';
    for (let i = 0; i < parts.length; i++) {
        if (parts[i].startsWith('@')) {
            username = parts[i].substring(1); // '@' 제외한 부분이 유저 이름
            break;
        }
    }

    const blogUrl = 'http://localhost/@' + username;

    window.location.href = blogUrl;
});

$('#series-btn').click(function () {
    const seriesId = $('#series-id').val();

    const currentUrl = window.location.href;
    const parts = currentUrl.split('/');

    let username = '';
    for (let i = 0; i < parts.length; i++) {
        if (parts[i].startsWith('@')) {
            username = parts[i].substring(1); // '@' 제외한 부분이 유저 이름
            break;
        }
    }

    const seriesUrl = 'http://localhost/@' + username + '/series/' + seriesId;

    window.location.href = seriesUrl;
});

$('.deleteBtn').click(function () {
    const postId = $('#post-id').val();

    $.ajax({
        url: "/api/posts/" + postId,
        type: 'DELETE',
        success: function (response) {
            window.location.href = "/";
        },
        error: function (error) {
            console.error('에러발생:', error);
        }
    });
});

$('.editBtn').click(function () {
    const postId = $('#post-id').val();
    window.location.href = '/post/write?postId=' + postId;
});

//==================

$(document).ready(function () {
    addViewCount();
    loadComment();
});

function loadComment(page) {
    const commentElement = $('#comment-list');
    const postId = $('#post-id').val();

    console.log(page);

    commentElement.empty();
    $.ajax({
        url: '/api/comments',
        type: 'GET',
        data: {
            'postId': postId,
            'page': page
        },
        success: function (response) {


            response.comments.forEach(function (comment) {
                const formattedDate = moment(comment.registrationDate).format('YYYY/MM/DD HH:mm');

                const html = `
                    <div class="comment">
                        <input type="hidden" class="comment-id" value=${comment.id}>
                        <a class="writer" href="${'/@' + comment.writerUsername}">
                            <strong>${comment.writerName}</strong>
                        </a>
                        <div class="edit">
                            ${comment.writer ? `
                                <input type="button" class="edit-comment-btn" value="수정">
                                <input type="button" class="delete-comment-btn" value="삭제">
                            ` : ``}
                        </div>
                        <div class="content">${comment.content}</div>
                        <div class="registration-date right">${formattedDate}</div>
                        <div class="sub-comment-btn right">
                            <button class="show-sub-comment-btn btn btn-outline-dark">대댓글 보기</button>
                            <button class="add-sub-comment-btn btn btn-outline-dark">대댓글 등록하기</button>
                        </div>
                        <div class="sub-comment-list"></div>
                    </div>
                `
                commentElement.append(html);
            })
            setupPagination(response.currentPage, response.totalPages);
        },
        error: function (error) {
            commentElement.append('<div id="no-comments" class="center">댓글이 없습니다</div>')
        }
    });
}

function setupPagination(currentPage, totalPages) {
    const commentElement = $('#comment-list');

    const paginationElement = $('<div id="pagination" class="center"></div>');

    if (totalPages > 1) {
        for (let i = 1; i <= totalPages; i++) {
            const pageButton = $(`<button class="btn btn-outline-dark">${i}</button>`);
            if (i === currentPage) {
                pageButton.addClass('active');
            }
            pageButton.on('click', function () {
                loadComment(i);
            });
            paginationElement.append(pageButton);
        }
    }

    commentElement.append(paginationElement);
}

$('#comment-summit-btn').click(function () {
    const content = $('#comment-input').val();
    const postId = $('#post-id').val();

    $.ajax({
        url: "/api/comments",
        type: 'POST',
        data: {
            'postId': postId,
            'content': content
        },
        success: function (response) {
            loadComment()
        },
        error: function (error) {
            if (error.status === 401) {
                // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
                window.location.href = '/login';
            } else {
                console.error('에러발생:', error);
            }
        }
    });
});

// =====================

$(document).on('click', '.edit-comment-btn', function () {
    const commentElement = $(this).closest('.comment');
    const oldText = commentElement.find('.content').text();

    const html = `
                    <div class="edit-comment-input">
                        <textarea class="comment-input form-control" maxlength="200" placeholder="최대 200자">${oldText}</textarea>
                        <div class="edit-comment-btn right">
                            <button class="cancel-edit-comment-btn btn btn-outline-dark">취소 하기</button>
                            <button class="summit-edit-comment-btn btn btn-outline-dark">수정 하기</button>
                        </div>
                    </div>
                `
    commentElement.find('.content').hide();
    commentElement.find('.edit-comment-btn').hide();
    commentElement.find('.registration-date').hide();
    commentElement.find('.add-sub-comment-btn').hide();

    commentElement.append(html);
});

$(document).on('click', '.cancel-edit-comment-btn', function () {
    const commentElement = $(this).closest('.comment');
    commentElement.find('.content').show();
    commentElement.find('.edit-comment-btn').show();
    commentElement.find('.registration-date').show();
    commentElement.find('.add-sub-comment-btn').show();
    commentElement.find('.edit-comment-input').remove();
});

$(document).on('click', '.summit-edit-comment-btn', function () {
    const commentElement = $(this).closest('.comment');
    const newText = commentElement.find('.comment-input').val();
    const commentId = commentElement.find('.comment-id').val();

    $.ajax({
        url: "/api/comments",
        type: 'PUT',
        data: {
            'commentId': commentId,
            'content': newText
        },
        success: function (response) {
            commentElement.find('.content').text(newText);
        },
        error: function (error) {
            alert('댓글 수정 실패');
        }
    });

    commentElement.find('.content').show();
    commentElement.find('.edit-comment-btn').show();
    commentElement.find('.registration-date').show();
    commentElement.find('.add-sub-comment-btn').show();
    commentElement.find('.edit-comment-input').remove();
});

$(document).on('click', '.delete-comment-btn', function () {
    const commentElement = $(this).closest('.comment');
    const commentId = commentElement.find('.comment-id').val();

    $.ajax({
        url: "/api/comments",
        type: 'DELETE',
        data: {
            'commentId': commentId
        },
        success: function (response) {
            loadComment()
        },
        error: function (error) {
            alert('댓글 삭제 실패');
        }
    });
});

// =================================
let showSub = false;

$(document).on('click', '.add-sub-comment-btn', function () {
    const commentElement = $(this).closest('.comment');
    $(this).hide();

    const subCommentHtml = `
        <div class="sub-comment-form">
            <textarea class="sub-comment-input form-control" placeholder="최대 200자"></textarea>
            <div class="right">
                <button class="btn btn-outline-dark cancel-sub-comment-btn">취소 하기</button>
                <button class="btn btn-outline-dark submit-sub-comment-btn">등록 하기</button>
            </div>
        </div>
    `;
    commentElement.append(subCommentHtml);
});

$(document).on('click', '.cancel-sub-comment-btn', function () {
    const commentElement = $(this).closest('.comment');
    commentElement.find('.add-sub-comment-btn').show();
    commentElement.find('.sub-comment-input').remove();
});

$(document).on('click', '.submit-sub-comment-btn', function () {
    const commentElement = $(this).closest('.comment');
    const content = commentElement.find('.sub-comment-input').val();
    const commentId = commentElement.find('.comment-id').val();
    const postId = $('#post-id').val();

    console.log(content);

    $.ajax({
        url: "/api/comments",
        type: 'POST',
        data: {
            'postId': postId,
            'content': content,
            'parentId': commentId
        },
        success: function (response) {
            loadComment()
        },
        error: function (error) {
            if (error.status === 401) {
                // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
                window.location.href = '/login';
            } else {
                alert("대댓글 작성에 실패했습니다.")
            }
        }
    });
});

$(document).on('click', '.show-sub-comment-btn', function () {

    const commentElement = $(this).closest('.comment');
    const commentId = commentElement.find('.comment-id').val();
    const subCommentElement = commentElement.find('.sub-comment-list');

    if (showSub) {
        subCommentElement.empty();
        showSub = !showSub;
        return;
    }

    $.ajax({
        url: "/api/comments/sub",
        type: 'GET',
        data: {
            'parentId': commentId
        },
        success: function (response) {
            response.comments.forEach(function (comment) {
                const formattedDate = moment(comment.registrationDate).format('YYYY/MM/DD HH:mm');

                const html = `
                    <div class="comment">
                        <input type="hidden" class="comment-id" value=${comment.id}>
                        <a class="writer" href="${'/@' + comment.writerUsername}">
                            <strong>${comment.writerName}</strong>
                        </a>
                        <div class="edit">
                            ${comment.writer ? `
                                <input type="button" class="edit-comment-btn" value="수정">
                                <input type="button" class="delete-comment-btn" value="삭제">
                            ` : ``}
                        </div>
                        <div class="content">${comment.content}</div>
                        <div class="registration-date right">${formattedDate}</div>
                    </div>
                `
                subCommentElement.append(html);
            })
        },
        error: function (error) {
            subCommentElement.append('<div id="no-sub-comments" class="center">대댓글이 없습니다</div>')
        }
    });
    showSub = !showSub;
});

// =====
function addViewCount() {
    if ($('#edit').length !== 0) {
        console.log("writer");
        return;
    }

    const postId = $('#post-id').val();

    $.ajax({
        url: "/api/posts/view",
        type: 'PUT',
        data: {
            'postId': postId
        },
        success: function (response) {
            const currentViewCount = parseInt($('#view-count').text().replace('조회수 ', ''), 10);
            const newViewCount = currentViewCount + 1;
            $('#view-count').text('조회수 ' + newViewCount);
        },
        error: function (error) {
        }
    });
}