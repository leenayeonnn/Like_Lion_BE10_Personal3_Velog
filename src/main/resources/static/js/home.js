document.addEventListener('DOMContentLoaded', function () {
    const postContainer = document.getElementById('postContainer');
    let page = 0;
    let loading = false;

    function fetchPosts() {
        if (loading) return;
        loading = true;
        fetch('/api/posts?page=' + page)
            .then(response => response.json())
            .then(data => {
                data.forEach(post => {
                    const postHtml = `
                        <div class="col mb-5">
                            <div class="post h-100">
                                <!-- Product image-->
                                <img class="post-img-top" src="https://dummyimage.com/450x300/dee2e6/6c757d.jpg" alt="..."/>
                                    <!-- Product details-->
                                <div class="post-body p-4">
                                    <div class="text-center">
                                        <!-- Product name-->
                                        <h5 class="fw-bolder">Fancy Product</h5>
                                        <!-- Product price-->
                                        $40.00 - $80.00
                                    </div>
                                </div>
                                <!-- Product actions-->
                                <div class="post-footer p-4 pt-0 border-top-0 bg-transparent">
                                    <div class="text-center"><a class="btn btn-outline-dark mt-auto" href="#">View options</a></div>
                                </div>
                            </div>
                        </div>
                        `;
                    postContainer.insertAdjacentHTML('beforeend', postHtml);
                });
                page++;
                loading = false;
                loadingIndicator.style.display = 'none';
            })
            .catch(error => {
                console.error('Error fetching posts:', error);
                loading = false;
                loadingIndicator.style.display = 'none';
            });
    }

    // IntersectionObserver를 사용하여 스크롤 감지
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                fetchPosts();
                loadingIndicator.style.display = 'block';
            }
        });
    }, {
        threshold: 0.5 // 스크롤 위치 감지 임계값 설정
    });

    // 페이지 로드 시 초기 데이터 로딩
    fetchPosts();
    observer.observe(loadingIndicator);
});