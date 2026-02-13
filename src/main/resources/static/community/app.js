const { createApp, ref, reactive, computed, onMounted } = Vue;

createApp({
  setup() {
    const me = ref(null);
    const posts = ref([]);            // 전체 페이지 조회 결과(서버 기반)
    const myPosts = ref([]);          // 내 글 전체(클라 필터)
    const selectedPost = ref(null);
    const commentsMap = reactive({});
    const commentDraft = ref('');

    const page = ref(1);
    const PAGE_SIZE = 10;
    const hasNextPage = ref(false);
    const postViewMode = ref('all'); // all | mine

    const postSearchId = ref('');
    const searchedPost = ref(null);

    const loading = reactive({ posts: false, createPost: false, createComment: false });

    const loginForm = reactive({ username: '', password: '' });
    const signupForm = reactive({ username: '', password: '', nickname: '', email: '' });
    const postForm = reactive({ title: '', content: '' });
    const profileForm = reactive({ nickname: '', email: '', password: '' });

    const authModal = reactive({ open: false, mode: 'login' });
    const toast = reactive({ show: false, ok: true, message: '' });
    const errorPopup = reactive({ show: false, message: '' });
    let errorTimer = null;

    function showToast(ok, message) {
      toast.ok = ok;
      toast.message = message;
      toast.show = true;
      setTimeout(() => { toast.show = false; }, 2200);
    }

    function showError(message) {
      errorPopup.message = message;
      errorPopup.show = true;

      if (errorTimer) clearTimeout(errorTimer);
      errorTimer = setTimeout(() => {
        closeErrorPopup();
      }, 3000);
    }

    function closeErrorPopup() {
      if (errorTimer) {
        clearTimeout(errorTimer);
        errorTimer = null;
      }
      errorPopup.show = false;
      errorPopup.message = '';
    }

    function openAuth(mode) {
      authModal.mode = mode;
      authModal.open = true;
    }

    async function api(path, options = {}) {
      const res = await fetch(path, {
        credentials: 'include',
        headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
        ...options,
      });

      let body = null;
      try { body = await res.json(); } catch (_) {}

      if (!res.ok || (body && body.success === false)) {
        throw new Error(body?.message || `요청 실패 (${res.status})`);
      }

      return body;
    }

    function fillProfileForm() {
      profileForm.nickname = me.value?.nickname || '';
      profileForm.email = me.value?.email || '';
      profileForm.password = '';
    }

    async function loadMe() {
      try {
        const r = await api('/api/me');
        me.value = r.data;
        fillProfileForm();
      } catch (_) {
        me.value = null;
      }
    }

    async function loadPosts(targetPage = page.value) {
      loading.posts = true;
      try {
        const r = await api(`/api/posts?page=${targetPage}&limit=${PAGE_SIZE}`);
        posts.value = r.data || [];
        hasNextPage.value = posts.value.length === PAGE_SIZE;
      } catch (e) {
        showError(e.message);
      } finally {
        loading.posts = false;
      }
    }

    async function loadMyPosts() {
      if (!me.value) {
        myPosts.value = [];
        return;
      }
      try {
        // 서버에 "내 글" 전용 API가 없어서 전체를 가져와 작성자 기준으로 필터
        const r = await api('/api/posts?page=1&limit=1000');
        myPosts.value = (r.data || []).filter((p) => p.userId === me.value.id);
      } catch (e) {
        showError(e.message);
      }
    }

    const displayedPosts = computed(() => {
      if (postViewMode.value === 'all') return posts.value;

      const start = (page.value - 1) * PAGE_SIZE;
      const end = start + PAGE_SIZE;
      return myPosts.value.slice(start, end);
    });

    function calcMineHasNext() {
      return page.value * PAGE_SIZE < myPosts.value.length;
    }

    async function switchViewMode(mode) {
      postViewMode.value = mode;
      page.value = 1;

      if (mode === 'all') {
        await loadPosts(1);
      } else {
        if (!me.value) return showError('로그인이 필요합니다');
        await loadMyPosts();
        hasNextPage.value = calcMineHasNext();
      }
    }

    function prevPage() {
      if (page.value <= 1) return;
      page.value -= 1;

      if (postViewMode.value === 'all') {
        loadPosts(page.value);
      } else {
        hasNextPage.value = calcMineHasNext();
      }
    }

    function nextPage() {
      if (!hasNextPage.value) return;
      page.value += 1;

      if (postViewMode.value === 'all') {
        loadPosts(page.value);
      } else {
        hasNextPage.value = calcMineHasNext();
      }
    }

    async function searchPostById() {
      if (!postSearchId.value) return showError('게시글 번호를 입력해 주세요');
      try {
        const r = await api(`/api/posts/${postSearchId.value}`);
        searchedPost.value = r.data;
      } catch (e) {
        searchedPost.value = null;
        showError(e.message);
      }
    }

    async function login() {
      try {
        await api('/api/login', { method: 'POST', body: JSON.stringify(loginForm) });
        await loadMe();
        authModal.open = false;
        await refreshAll();
        showToast(true, '로그인 성공');
      } catch (e) {
        showError(e.message);
      }
    }

    async function signup() {
      try {
        await api('/api/users', { method: 'POST', body: JSON.stringify(signupForm) });
        authModal.mode = 'login';
        showToast(true, '회원가입 완료! 로그인해 주세요.');
      } catch (e) {
        showError(e.message);
      }
    }

    async function logout() {
      try {
        await api('/api/logout', { method: 'POST' });
        me.value = null;
        selectedPost.value = null;
        postViewMode.value = 'all';
        page.value = 1;
        await loadPosts(1);
        showToast(true, '로그아웃 되었습니다');
      } catch (e) {
        showError(e.message);
      }
    }

    async function createPost() {
      if (!me.value) return showError('로그인이 필요합니다');
      if (!postForm.title || !postForm.content) return showError('제목/내용을 입력해 주세요');

      loading.createPost = true;
      try {
        await api('/api/posts', {
          method: 'POST',
          body: JSON.stringify({ title: postForm.title, content: postForm.content }),
        });
        postForm.title = '';
        postForm.content = '';
        await refreshAll();
        showToast(true, '게시글이 등록되었습니다');
      } catch (e) {
        showError(e.message);
      } finally {
        loading.createPost = false;
      }
    }

    async function openPost(postId) {
      try {
        const r = await api(`/api/posts/${postId}`);
        selectedPost.value = r.data;
        await loadComments(postId);
      } catch (e) {
        showError(e.message);
      }
    }

    async function deletePost(postId) {
      if (!me.value) return showError('로그인이 필요합니다');
      if (!confirm('게시글을 삭제할까요?')) return;

      try {
        await api(`/api/posts/${postId}`, { method: 'DELETE' });
        if (selectedPost.value?.id === postId) selectedPost.value = null;
        await refreshAll();
        showToast(true, '게시글 삭제 완료');
      } catch (e) {
        showError(e.message);
      }
    }

    async function loadComments(postId) {
      try {
        const r = await api(`/api/posts/${postId}/comments?limit=50`);
        commentsMap[postId] = r.data || [];
      } catch (e) {
        showError(e.message);
      }
    }

    async function createComment(postId) {
      if (!me.value) return showError('로그인이 필요합니다');
      if (!commentDraft.value) return showError('댓글을 입력해 주세요');

      loading.createComment = true;
      try {
        await api(`/api/posts/${postId}/comments`, {
          method: 'POST',
          body: JSON.stringify({ content: commentDraft.value }),
        });
        commentDraft.value = '';
        await loadComments(postId);
        await refreshAll();
        if (selectedPost.value?.id === postId) {
          const latest = await api(`/api/posts/${postId}`);
          selectedPost.value = latest.data;
        }
        showToast(true, '댓글 등록 완료');
      } catch (e) {
        showError(e.message);
      } finally {
        loading.createComment = false;
      }
    }

    async function deleteComment(commentId, postId) {
      if (!me.value) return showError('로그인이 필요합니다');
      if (!confirm('댓글을 삭제할까요?')) return;

      try {
        await api(`/api/comments/${commentId}`, { method: 'DELETE' });
        await loadComments(postId);
        await refreshAll();
        if (selectedPost.value?.id === postId) {
          const latest = await api(`/api/posts/${postId}`);
          selectedPost.value = latest.data;
        }
        showToast(true, '댓글 삭제 완료');
      } catch (e) {
        showError(e.message);
      }
    }

    async function changeNickname() {
      if (!me.value) return showError('로그인이 필요합니다');
      if (!profileForm.nickname) return showError('새 닉네임을 입력해 주세요');

      try {
        await api(`/api/users/${me.value.id}/nickname`, {
          method: 'PUT',
          body: JSON.stringify({ nickname: profileForm.nickname }),
        });
        await loadMe();
        showToast(true, '닉네임이 변경되었습니다');
      } catch (e) {
        showError(e.message);
      }
    }

    async function changeEmail() {
      if (!me.value) return showError('로그인이 필요합니다');
      if (!profileForm.email) return showError('새 이메일을 입력해 주세요');

      try {
        await api(`/api/users/${me.value.id}/email`, {
          method: 'PUT',
          body: JSON.stringify({ email: profileForm.email }),
        });
        await loadMe();
        showToast(true, '이메일이 변경되었습니다');
      } catch (e) {
        showError(e.message);
      }
    }

    async function changePassword() {
      if (!me.value) return showError('로그인이 필요합니다');
      if (!profileForm.password) return showError('새 비밀번호를 입력해 주세요');

      try {
        await api(`/api/users/${me.value.id}/password`, {
          method: 'PUT',
          body: JSON.stringify({ password: profileForm.password }),
        });
        profileForm.password = '';
        showToast(true, '비밀번호가 변경되었습니다');
      } catch (e) {
        showError(e.message);
      }
    }

    async function refreshAll() {
      await loadMe();

      if (postViewMode.value === 'all') {
        await loadPosts(page.value);
      } else {
        await loadMyPosts();
        hasNextPage.value = calcMineHasNext();
      }

      if (selectedPost.value?.id) {
        await openPost(selectedPost.value.id);
      }
      showToast(true, '새로고침 완료');
    }

    const selectedComments = computed(() => {
      if (!selectedPost.value) return [];
      return commentsMap[selectedPost.value.id] || [];
    });

    onMounted(async () => {
      await loadMe();
      await loadPosts(1);
    });

    return {
      me,
      posts,
      myPosts,
      displayedPosts,
      selectedPost,
      selectedComments,
      commentDraft,
      page,
      hasNextPage,
      postViewMode,
      postSearchId,
      searchedPost,
      loading,
      loginForm,
      signupForm,
      postForm,
      profileForm,
      authModal,
      toast,
      errorPopup,
      openAuth,
      closeErrorPopup,
      login,
      signup,
      logout,
      createPost,
      openPost,
      deletePost,
      createComment,
      deleteComment,
      changeNickname,
      changeEmail,
      changePassword,
      refreshAll,
      switchViewMode,
      prevPage,
      nextPage,
      searchPostById,
    };
  },
}).mount('#app');
