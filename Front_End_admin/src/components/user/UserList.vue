<template>
    <div class="user-list-page">
        <!-- 제목 -->
        <h3 class="page-title">USER LIST</h3>
        <hr />

        <!-- 검색 바 컴포넌트 -->
        <UserSearchInput />

        <!-- 테이블 -->
        <table class="user-table">
            <thead>
                <tr>
                    <th>아이디</th>
                    <th>이름</th>
                    <th>이메일</th>
                    <th>생일</th>
                    <th>성별</th>
                    <th>가입 여부</th>
                </tr>
            </thead>
            <tbody>
                <tr v-if="store.userList.length === 0">
                    <td colspan="6" class="no-data">검색 결과가 없습니다.</td>
                </tr>
                <tr v-for="user in paginatedUsers" :key="user.userLoginId">
                    <td>{{ user.userLoginId }}</td>
                    <td>{{ user.username }}</td>
                    <td>{{ user.email }}</td>
                    <td>{{ formatDate(user.birthDate) }}</td>
                    <td>{{ user.gender === 'M' ? '남성' : '여성' }}</td>
                    <td>{{ user.available === 1 ? 'O' : 'X' }}</td>
                </tr>
            </tbody>
        </table>

        <!-- 페이징 -->
        <div class="pagination">
            <button 
                :disabled="currentPage === 1" 
                @click="changePage(currentPage - 1)"
            >
                &lt;
            </button>
            <button 
                v-for="page in totalPages" 
                :key="page" 
                :class="{ active: currentPage === page }"
                @click="changePage(page)"
            >
                {{ page }}
            </button>
            <button 
                :disabled="currentPage === totalPages" 
                @click="changePage(currentPage + 1)"
            >
                &gt;
            </button>
        </div>
    </div>
</template>


<script setup>
import { useAdminStore } from "@/store/useAdminStore";
import { computed, onMounted, ref } from "vue";
import UserSearchInput from "./userSearchInput.vue";

// 스토어 초기화
const store = useAdminStore();
onMounted(() => {
    store.getUserList();
});

const currentPage = ref(1); 
const itemsPerPage = 8; 

const paginatedUsers = computed(() => {
    const startIndex = (currentPage.value - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    return store.userList.slice(startIndex, endIndex);
});

const totalPages = computed(() => Math.ceil(store.userList.length / itemsPerPage));

const changePage = (page) => {
    if (page > 0 && page <= totalPages.value) {
        currentPage.value = page;
    }
};

function formatDate(dateString) {
    return new Date(dateString).toISOString().split("T")[0];
}
</script>


<style scoped>
.user-list-page {
    padding: 20px;
    background-color: #fcfcfc;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.page-title {
    margin-bottom: 20px;
    color: #000000;
}

.user-table {
    width: 100%;
    border-collapse: collapse;
    background-color: #ffffff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-table thead {
    text-align: center;
    background-color: #043873;
    color: #ffffff;
}

.user-table th {
    padding: 10px;
    text-align: center;
    border-bottom: 1px solid #ddd;
}

.user-table td {
    padding: 10px;
    text-align: center;
    border-bottom: 1px solid #ddd;
}

.user-table tr:last-child td {
    border-bottom: none;
}

.user-table .no-data {
    text-align: center;
    font-style: italic;
    color: #999;
}

/* 페이징 */
.pagination {
    margin-top: 20px;
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
}

.pagination button {
    margin: 0 5px;
    padding: 5px 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    background-color: #fff;
    cursor: pointer;
    transition: all 0.2s;
}

.pagination button.active {
    background-color: #043873;
    color: #fff;
    font-weight: bold;
}

.pagination button:disabled {
    background-color: #f1f1f1;
    color: #ccc;
    cursor: not-allowed;
}
</style>

