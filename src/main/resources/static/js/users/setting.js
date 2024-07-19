$('#withdrawal-btn').click(function () {
    const withdrawalElement = $('#nav-withdrawal');
    withdrawalElement.empty();

    const html = `
        <p>회원 탈퇴시 저장된 정보가 영구 삭제됨을 알림니다.</p>
        <p>탈퇴를 원하시면 계정 비밀번호를 입력해 주세요.</p>
        <input id="withdrawal-password" type="password" name="password">
        <button id="submit-withdrawal-btn">탈퇴하기</button>
    `

    withdrawalElement.append(html);
});

$(document).on('click', '#submit-withdrawal-btn', function () {
    const password = $('#withdrawal-password').val();

    $.ajax({
        url: "/api/users/withdrawal",
        type: 'DELETE',
        data: {
            'password': password
        },
        success: function (response) {
            window.location.href = "/";
        },
        error: function (error) {
            const withdrawalElement = $('#nav-withdrawal');

            if (withdrawalElement.find('.error-message').length === 0) {
                withdrawalElement.append('<p class="error-message">비밀번호가 틀렸습니다.</p>')
            }
        }
    });
});

// ==============
$('#password-btn').click(function () {
    $(this).hide();
    const passwordElement = $(this).closest('.field');

    const html = `
        <div id="change-password">
            <label for="old-password">기존 PW</label>
            <input type="password" id="old-password">
            <br/>
            <label for="new-password">새 PW</label>
            <input type="password" id="new-password">
            <br/>
            <label for="new-password-check">새 PW 확인</label>
            <input type="password" id="new-password-check">
            <br/>
            <div class="right">
                <button id="cancel-change-password">취소하기</button>
                <button id="submit-change-password">변경하기</button>
            </div>
        </div>
    `
    passwordElement.append(html);
});

$(document).on('click', '#cancel-change-password', function () {
    $('#change-password').remove();
    $('#password-btn').show();
});

$(document).on('click', '#submit-change-password', function () {
    const oldPassword = $('#old-password').val();
    const newPassword = $('#new-password').val();
    const newPasswordCheck = $('#new-password-check').val();

    const changePasswordElement = $('#change-password');
    changePasswordElement.find('.error-message').remove();

    if (oldPassword === '' || newPassword === '' || newPasswordCheck === '') {
        changePasswordElement.append('<p class="error-message right">모두 입력해주세요</p>');
        return;
    }

    $.ajax({
        url: "/api/users/password",
        type: 'PUT',
        data: {
            'oldPassword': oldPassword,
            'newPassword': newPassword,
            'newPasswordCheck': newPasswordCheck
        },
        success: function (response) {
            changePasswordElement.remove();
            $('#password-btn').show();
            alert("비밀번호가 변경되었습니다.");
        },
        error: function (error) {

            const errorMsg = error.responseText;

            if (errorMsg === 'oldPassword') {
                changePasswordElement.append('<p class="error-message right">기존 비밀번호가 틀렸습니다.</p>')
            } else if (errorMsg === 'pattern') {
                changePasswordElement.append('<p class="error-message right">비밀번호는 영문자, 숫자, 특수문자를 포함하여 8~16자여야 합니다.</p>')
            } else {
                changePasswordElement.append('<p class="error-message right">새 비밀번호가 일치하지 않습니다.</p>')
            }

        }
    });
});

// =================
$('#profile-btn').click(function () {
    $(this).hide();
    const passwordElement = $(this).closest('.field');

    const html = `
        <div id="change-profile-img">
            <input type="file" id="uploadMainImg" value="이미지업로드"/>
            <div class="image-container">
                <img id="change-img-preview" src="">
            </div>
            <div class="right">
                <button id="cancel-change-profile-img">취소하기</button>
                <button id="submit-change-profile-img">변경하기</button>
            </div>
        </div>
    `
    passwordElement.append(html);
});

$(document).on('click', '#cancel-change-profile-img', function () {
    $('#change-profile-img').remove();
    $('#profile-btn').show();
});

$(document).on('change', '#uploadMainImg', function () {
    let formData = new FormData();
    let file = $("#uploadMainImg")[0].files[0];
    formData.append("file", file);

    $.ajax({
        url: '/api/users/upload-profile', // 파일 업로드 경로
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            $("#change-img-preview").attr("src", response.url);
        },
        error: function (error) {
        }
    });
});

$(document).on('click', '#submit-change-profile-img', function () {
    const imgUrl = $("#change-img-preview").attr('src');
    const changeProfileElement = $('#change-profile-img');
    changeProfileElement.find('.error-message').remove();

    if (imgUrl === '') {
        changeProfileElement.append('<p class="error-message right">이미지를 선택해주세요</p>');
        return;

    }

    $.ajax({
        url: "/api/users/profile",
        type: 'PUT',
        data: {
            'imgUrl': imgUrl
        },
        success: function (response) {
            changeProfileElement.remove();
            $('#profile-btn').show();
            $('#img-preview').attr('src', imgUrl);
            alert("프로필이 변경되었습니다.")
        },
        error: function (error) {
        }
    });
});

// =======================
$('#name-btn').click(function () {
    $(this).hide();
    const nameElement = $(this).closest('.field');
    const currentNameElement = $('#current-name');
    const oldName = currentNameElement.text();
    currentNameElement.hide();

    const html = `
        <div id="change-name">
            <label for="new-name">name</label>
            <input type="text" id="new-name">
            <br/>
            <div class="right">
                <button id="cancel-change-name">취소하기</button>
                <button id="submit-change-name">변경하기</button>
            </div>
        </div>
    `
    nameElement.append(html);
    $('#new-name').val(oldName);
});

$(document).on('click', '#cancel-change-name', function () {
    $('#change-name').remove();
    $('#name-btn').show();
    $('#current-name').show();
});

$(document).on('click', '#submit-change-name', function () {
    const newName = $("#new-name").val();
    const changeNameElement = $('#change-name');
    changeNameElement.find('.error-message').remove();

    if (newName === '') {
        changeNameElement.append('<p class="error-message right">이름을 입력해주세요</p>');
        return;
    }

    $.ajax({
        url: "/api/users/name",
        type: 'PUT',
        data: {
            'newName': newName
        },
        success: function (response) {
            changeNameElement.remove();
            $('#current-name').text(newName);
            $('#current-name').show();
            $('#name-btn').show();
            alert("이름이 변경되었습니다.")
        },
        error: function (error) {
        }
    });
});

// =======================
$('#email-btn').click(function () {
    $(this).hide();
    const emailElement = $(this).closest('.field');
    const currentEmailElement = $('#current-email');
    const oldEmail = currentEmailElement.text();
    currentEmailElement.hide();

    const html = `
        <div id="change-email">
            <label for="new-email">email</label>
            <input type="text" id="new-email">
            <br/>
            <div class="right">
                <button id="cancel-change-email">취소하기</button>
                <button id="submit-change-email">변경하기</button>
            </div>
        </div>
    `
    emailElement.append(html);
    $('#new-email').val(oldEmail);
});

$(document).on('click', '#cancel-change-email', function () {
    $('#change-email').remove();
    $('#email-btn').show();
    $('#current-email').show();
});

$(document).on('click', '#submit-change-email', function () {
    const newEmail = $("#new-email").val();
    const changeEmailElement = $('#change-email');
    changeEmailElement.find('.error-message').remove();

    if (newEmail === '') {
        changeEmailElement.append('<p class="error-message right">이메일을 입력해주세요</p>');
        return;
    }

    $.ajax({
        url: "/api/users/email",
        type: 'PUT',
        data: {
            'newEmail': newEmail
        },
        success: function (response) {
            changeEmailElement.remove();
            $('#current-email').text(newEmail);
            $('#current-email').show();
            $('#email-btn').show();
            alert("이메일이 변경되었습니다.")
        },
        error: function (error) {
            const errorMsg = error.responseText;

            if (errorMsg === 'pattern') {
                changeEmailElement.append('<p class="error-message right">이메일 형식으로 입력해주세요</p>')
            } else if (errorMsg === 'duplicate') {
                changeEmailElement.append('<p class="error-message right">중복된 이메일 입니다</p>')
            }
        }
    });
});