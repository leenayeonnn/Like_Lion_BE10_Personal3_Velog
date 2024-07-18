let postPage = 1;
let seriesPage = 1;
const blogUsername = $('#blog-username').val();
const loginUsername = $('#login-username').val();

$(document).ready(function () {
    loadInfo();
    loadPosts();
    loadSeries();
});

function loadInfo() {
    $.ajax({
        url: '/api/blogs',
        type: 'GET',
        data: {
            username: blogUsername
        },
        success: function (response) {
            const infoElement = $("#info");

            const html = `
                ${response.profileUrl != null ? `
                    <div id="info-profile">
                        <img id="profile-img" src="${response.profileUrl}" alt="">
                    </div>
                ` : ``}
                <div id="info-blogName">
                    <h2>${response.blogName}</h2>
                </div>
            `;

            infoElement.append(html);

            const introductionElement = $("#nav-introduction");

            loadIntroductionHtml(introductionElement, response.blogDescription);
        },
        error: function (error) {
        }
    });
}

$('#nav-introduction').on('click', '#edit-introduction-btn', function () {
    const introductionElement = $("#nav-introduction");
    const oldIntroductionContent = $("#introduction-content").text();
    introductionElement.empty();

    const html = `
        <div id="edit-introduction">
            <button id="save-introduction-btn" class="btn btn-dark">저장하기</button>
            <textarea id="introduction-input" maxlength="1000" placeholder="최대 1000자">${oldIntroductionContent}</textarea>
        </div>
    `

    introductionElement.append(html);
});

$('#nav-introduction').on('click', '#save-introduction-btn', function () {
    const description = $('#introduction-input').val();
    const formData = new FormData();
    formData.append("username", blogUsername);
    formData.append("description", description);

    $.ajax({
        url: '/api/blogs/description',
        type: 'PUT',
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
            const introductionElement = $("#nav-introduction");
            introductionElement.empty();
            loadIntroductionHtml(introductionElement, response.description);
        },
        error: function (error) {
        }
    });
});

function loadIntroductionHtml(element, description) {

    const html = `
        ${description === null || description === "" ?
        (loginUsername === blogUsername ? `
                <div id="no-introduction">
                    <button id="edit-introduction-btn" class="btn btn-dark">소개 작성하기</button>
                </div>` : `
                <div id="no-introduction">
                    <p>소개가 없습니다.</p>
                </div>`) :
        `<div id="introduction">
                ${loginUsername === blogUsername ? `
                    <button id="edit-introduction-btn" class="btn btn-dark">소개 수정하기</button>` : ''}
                <p id="introduction-content">${description}</p>
        </div>`
    }
    `;

    element.append(html);
}

// ==============

function loadPosts() {
    const postElement = $("#nav-post");

    $.ajax({
        url: '/api/posts/' + blogUsername + '?page=' + postPage, // 파일 업로드 경로
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
                postElement.append(postHtml);
            });

            postElement.append('<button id="load-more-post-btn" class="btn btn-dark">더 불러오기</button>')
        },
        error: function (error) {
            postElement.append('<div id="no-posts" class="center">게시물이 없습니다</div>');
        }
    });
}

$('#nav-post').on('click', '#load-more-post-btn', function () {
    $(this).remove(); // 현재 버튼 제거
    postPage++;
    loadPosts();
});

// ============================

function loadSeries() {
    const seriesElement = $("#nav-series");

    $.ajax({
        url: '/api/series/' + blogUsername + '?page=' + seriesPage, // 파일 업로드 경로
        type: 'GET',
        success: function (response) {
            response.series.forEach(function (series) {

                const seriesHtml = `
                    <a href="/@${blogUsername}/series/${series.id}">
                            <div class="series" data-series-id="${series.id}">                         
                                <div class="series-name">
                                    <h2>${series.name}</h2>
                                </div>
                            </div>
                    </a>
                        `;
                seriesElement.append(seriesHtml);
            });

            seriesElement.append('<button id="load-more-series-btn" class="btn btn-dark">더 불러오기</button>')
        },
        error: function (error) {
            seriesElement.append('<div id="no-series" class="center">시리즈가 없습니다</div>');
        }
    });
}

$('#nav-series').on('click', '#load-more-series-btn', function () {
    $(this).remove(); // 현재 버튼 제거
    seriesPage++;
    loadSeries();
});
