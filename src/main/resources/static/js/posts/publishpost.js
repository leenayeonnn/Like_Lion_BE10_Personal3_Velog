$("#uploadMainImg").change(function () {
    let formData = new FormData();
    let file = $("#uploadMainImg")[0].files[0];
    formData.append("file", file);

    $.ajax({
        url: '/api/posts/main-image', // 파일 업로드 경로
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            // 응답에서 URL을 가져와서 이미지를 표시하고 hidden input에 설정
            $("#mainImgPreview").attr("src", response.url);
            $("#mainImgUrl").val(response.url);
        },
        error: function (error) {
            console.error("Error uploading file:", error);
        }
    });
})

$(document).ready(function () {
    const blogId = $('#blogId').val();
    console.log(blogId);
    loadSeries(blogId);
});

function loadSeries(blogId) {
    $.ajax({
        url: "/api/series",
        type: 'GET',
        data: {
            blogId: blogId
        },
        success: function (response) {
            // 시리즈 데이터를 받아서 select 요소에 옵션으로 추가
            const selectElement = $('.form-select');

            selectElement.append('<option value="">없음</option>');
            response.forEach(function (seriesName) {
                selectElement.append('<option value="' + seriesName + '">' + seriesName + '</option>');
            });
        },
        error: function (error) {
            $('.error-message').text(error.statusText);
        }
    });
}

$(document).on('change', '#series', function () {
    let selectedSeries = $(this).val();
    $('#selectedSeries').val(selectedSeries);
});

$('#addSeriesBtn').click(function () {
    const seriesName = $('#addSeriesInput').val().trim();

    if (seriesName.length === 0) {
        return; // 시리즈 이름이 비어있으면 처리하지 않음
    }

    let formData = new FormData();
    const blogId = $('#blogId').val();

    formData.append("seriesName", seriesName);
    formData.append("blogId", blogId);

    $.ajax({
        url: "/api/series",
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            const selectElement = $('.form-select');
            selectElement.append('<option value="' + seriesName + '">' + seriesName + '</option>');
        },
        error: function (error) {
            const addSeriesElement = $('#addSeries');
            addSeriesElement.append('<small class="error-message">이미 존재하는 시리즈 입니다.</small>');
        }
    });

    $('#addSeriesInput').val('');

})