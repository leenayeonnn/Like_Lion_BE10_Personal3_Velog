document.addEventListener("DOMContentLoaded", function () {
    ClassicEditor.create(document.querySelector('#content'), {
        language: "ko",
        ckfinder: {
            uploadUrl: "/api/posts/image",
            withCredentials: true
        }
    });
});
