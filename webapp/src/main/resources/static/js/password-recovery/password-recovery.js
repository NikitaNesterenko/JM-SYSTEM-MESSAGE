import { ownFetch } from '/static/js/rest/fetch-service.js';
$(document).ready( function () {
    content();
});

const currentHref = window.location.href;
const resetPasswordHref = 'http://localhost:8080/password-recovery';

const content = () => {
    //setPasswordContent;/* resetPasswordContent;*/
    let currentContent = resetPasswordContent;

    console.log(currentHref);
    if (currentHref!==resetPasswordHref) {
        currentContent = setPasswordContent;
    }

    document.getElementById("main-div").innerHTML = currentContent;
};

const resetPasswordContent =
        `<div class="sign-in-text-1">
            Сброс пароля
        </div>
        <div class="sign-in-text-2">
            Для сброса пароля введите email указанный
            при реистрации <b>JS-MESSAGE-SYSTEM URL</b>.
        </div>
        <div class="sign-in-input">
            <input class="your-workspace-url text-center" id="recovery-password" type="text" placeholder="you@example.com">
        </div>
        <div class="sign-in-btn-box" id="submit">
            <button class="sign-in-button" id="reset-password" type="submit" name="button">Получить ссылку для сброса пароля</button>
        </div>`;

const setPasswordContent =
        `<div class="sign-in-text-1">
            Сброс пароля
        </div>
        <div class="sign-in-text-2">
            Пожалуйста, введите новый пароль
        </div>
        <div class="sign-in-input">
            <input class="your-workspace-url" id="new-password" type="password" placeholder="your new password">
        </div>
           <div class="sign-in-input" type="password">
            <input class="your-workspace-url" id="new-password-confirm" type="password" placeholder="your new password">
        </div>
        <div class="sign-in-btn-box" id="submit">
            <button class="sign-in-button" id="set-password" type="submit" name="button">Установить новый пароль</button>
        </div>`;


window.addEventListener("load", function () {
    const submitButtonResetPassword = document.getElementById("reset-password");
    const submitButtonSetPassword = document.getElementById("set-password");

    if (submitButtonResetPassword!==null) {
        submitButtonResetPassword.onclick = function () {
            const input_field = document.getElementById("recovery-password");
            fetch('/rest/api/users/is-exist-email/' + input_field.value)
                .then(response => {
                    if (!response.ok) {
                        return alert(input_field.value + " данный email не существует");
                    }

                    return alert("Пожалуйста, проверьте вашу почту " + input_field.value);
                });
        };
    }

    if (submitButtonSetPassword) {
        submitButtonSetPassword.onclick = function () {
            const newPassword = document.getElementById("new-password").value;
            const newPasswordConfirm = document.getElementById("new-password-confirm").value;

            console.log(newPassword);
            console.log(newPasswordConfirm);


            if (newPassword !== newPasswordConfirm) {
                return alert("Пароли не совпадают");
            }

            console.log(currentHref);

            ownFetch.post('/rest/api/users/password-recovery', {
                headers: {
                    "Content-type": "application/x-www-form-urlencoded; charset=UTF-8"
                },
                body: 'token=' + currentHref + '&password='+ newPassword
            }).then(response => {
                if (!response.ok) {
                    return alert("Время действия ссылки сброса пароля закончилось \n или вы ввели некоректную сслыку");
                }

                return alert("Новый пароль установлен");
            });
        };
    }
});
