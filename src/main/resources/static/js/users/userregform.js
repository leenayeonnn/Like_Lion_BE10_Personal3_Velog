document.addEventListener("DOMContentLoaded", function () {
    let idValid = false;
    let emailValid = false;
    let passwordValid = false;
    let passwordMatch = false;

    function updateSignUpButtonState() {
        const signUpButton = document.getElementById('signUpButton');
        signUpButton.disabled = !(idValid && emailValid && passwordValid && passwordMatch);
    }

    document.getElementById('id').addEventListener('input', function () {
        idValid = false;
        updateSignUpButtonState();
        document.getElementById('idCheckResult').textContent = '';
    });

    document.getElementById('email').addEventListener('input', function () {
        emailValid = false;
        updateSignUpButtonState();
        document.getElementById('emailCheckResult').textContent = '';
    });

    document.getElementById('checkId').addEventListener('click', function () {
        const username = document.getElementById('id').value;
        const xhr = new XMLHttpRequest();
        xhr.open('GET', '/api/users/check-username?username=' + encodeURIComponent(username), true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                const resultElement = document.getElementById('idCheckResult');
                if (xhr.status === 200) {
                    resultElement.textContent = '사용 가능합니다';
                    resultElement.style.color = 'green';
                    idValid = true;
                    updateSignUpButtonState();
                } else {
                    resultElement.textContent = xhr.responseText;
                    resultElement.style.color = 'red';
                }
            }
        };
        xhr.send();
    });

    document.getElementById('checkEmail').addEventListener('click', function () {
        const email = document.getElementById('email').value;
        const xhr = new XMLHttpRequest();
        xhr.open('GET', '/api/users/check-email?email=' + encodeURIComponent(email), true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                const resultElement = document.getElementById('emailCheckResult');
                if (xhr.status === 200) {
                    resultElement.textContent = '사용 가능합니다';
                    resultElement.style.color = 'green';
                    emailValid = true;
                    updateSignUpButtonState();
                } else {
                    resultElement.textContent = xhr.responseText;
                    resultElement.style.color = 'red';
                }
            }
        };
        xhr.send();
    });

    document.getElementById('password').addEventListener('input', validatePassword);
    document.getElementById('passwordCheck').addEventListener('input', validatePasswordMatch);

    function validatePassword() {
        const password = document.getElementById('password').value;
        const resultElement = document.getElementById('passwordValidResult');
        const isValidPasswordFormat = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[a-zA-Z\d!@#$%^&*]{8,16}$/.test(password);

        if (isValidPasswordFormat) {
            resultElement.textContent = '유효한 형식입니다.';
            resultElement.style.color = 'green';
            passwordValid = true;
        } else {
            resultElement.textContent = '비밀번호는 영문자, 숫자, 특수문자를 포함하여 8~16자여야 합니다.';
            resultElement.style.color = 'red';
            passwordValid = false;
        }
        updateSignUpButtonState();
    }

    function validatePasswordMatch() {
        const password = document.getElementById('password').value;
        const passwordCheck = document.getElementById('passwordCheck').value;
        const resultElement = document.getElementById('passwordCheckResult');

        if (password && passwordCheck && password === passwordCheck) {
            resultElement.textContent = 'password 가 일치합니다.';
            resultElement.style.color = 'green';
            passwordMatch = true;
        } else {
            resultElement.textContent = 'password 가 일치하지 않습니다.';
            resultElement.style.color = 'red';
            passwordMatch = false;
        }
        updateSignUpButtonState();
    }
});
