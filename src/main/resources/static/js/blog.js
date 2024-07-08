document.addEventListener("DOMContentLoaded", function () {
    const blogOwner = document.getElementById('blog')
        .getAttribute('blogOwner');
    const loginUser = document.getElementById('blog')
        .getAttribute('loginUser');

    const xhrBlogInfo = new XMLHttpRequest();
    xhrBlogInfo.open('GET', `/api/users/${encodeURIComponent(blogOwner)}/blog`, true);
    xhrBlogInfo.setRequestHeader('Content-Type', 'application/json');

    xhrBlogInfo.onload = function () {
        if (xhrBlogInfo.status === 200) {
            console.log(xhrBlogInfo.responseText);
            const blog = JSON.parse(xhrBlogInfo.responseText);
            displayBlogInfo(blog);
        }
    };

    xhrBlogInfo.send();

    if (loginUser === blogOwner) {
        hideFollowButton(true);
    } else {
        hideFollowButton(false);

    }

    const xhrFollowing = new XMLHttpRequest();
    xhrFollowing.open('GET', `/api/users/${encodeURIComponent(blogOwner)}/followings`, true);
    xhrFollowing.setRequestHeader('Content-Type', 'application/json');

    xhrFollowing.onload = function () {
        let followingUsers;
        if (xhrFollowing.status === 200) {
            followingUsers = JSON.parse(xhrFollowing.responseText);
            displayFollowingCount(followingUsers);
        } else if (xhrFollowing.status === 204) {
            followingUsers = null;
            displayFollowingCount(followingUsers);
        } else {
            console.log("error");
        }
    };

    xhrFollowing.send();

    const xhrFollower = new XMLHttpRequest();
    xhrFollower.open('GET', `/api/users/${encodeURIComponent(blogOwner)}/followers`, true);
    xhrFollower.setRequestHeader('Content-Type', 'application/json');

    xhrFollower.onload = function () {
        let followerUsers;
        if (xhrFollower.status === 200) {
            followerUsers = JSON.parse(xhrFollower.responseText);
            displayFollowerCount(followerUsers);

            const isLoginUserFollower = followerUsers.some(user => user.username === loginUser);
            if (isLoginUserFollower) {
                followText(true);
            } else {
                followText(false);
            }

        } else if (xhrFollower.status === 204) {
            followerUsers = null;
            displayFollowerCount(followerUsers);
            followText(false);
        } else {
            console.log("error");
        }
    };

    xhrFollower.send();

    document.getElementById('follow-button').addEventListener('click', function () {
        var xhrClickFollow = new XMLHttpRequest();

        if (isFollow()) {
            xhrClickFollow.open('DELETE', `/api/users/${encodeURIComponent(blogOwner)}/follow`, true);
            xhrClickFollow.setRequestHeader('Content-Type', 'application/json');

            xhrClickFollow.onload = function () {
                followText(false);
            }
        } else {
            xhrClickFollow.open('POST', `/api/users/${encodeURIComponent(blogOwner)}/follow`, true);
            xhrClickFollow.setRequestHeader('Content-Type', 'application/json');

            // 헤더 설정 : 이게 restapi 호출한것임을 알려주는 역할
            xhrClickFollow.setRequestHeader("X-Requested-With", "XMLHttpRequest");

            // 팔로우 버튼 눌렀을 때
            xhrClickFollow.onload = function () {
                if (xhrClickFollow === 200) {
                    followText(true);
                } else if (xhrClickFollow.status === 401) { // 401 상태 코드 처리
                    // location 가져와 이동처리
                    window.location.href = xhrClickFollow.getResponseHeader('Location') + `?redirect=${window.location.pathname}`;
                }
            }

        }
        xhrClickFollow.send();
    });

});

function displayBlogInfo(blog) {
    const profileImg = document.getElementById('blog-profile');
    const name = document.getElementById('blog-name');
    const description = document.getElementById('blog-description');

    profileImg.src = blog.user.profileUrl;
    name.textContent = blog.name;
    description.textContent = blog.description;
}

function displayFollowingCount(followingUsers) {
    const following = document.getElementById('following-count');
    console.log('following');

    if (followingUsers == null) {
        following.textContent = 0;
    } else {
        following.textContent = followingUsers.length;
    }
}

function displayFollowerCount(followerUsers) {
    const follower = document.getElementById('follower-count');

    if (followerUsers == null) {
        follower.textContent = 0;
    } else {
        follower.textContent = followerUsers.length;
    }
}

function hideFollowButton(condition) {
    const followButton = document.getElementById('follow-button');
    followButton.hidden = condition;
}

function followText(condition) {
    const followButton = document.getElementById('follow-button');
    if (condition) {
        followButton.textContent = "팔로잉"
    } else {
        followButton.textContent = "팔로우"
    }
}

function isFollow() {
    return document.getElementById('follow-button').textContent === '팔로잉';
}