<template>
  <div class="signup-container">
    <div class="signup-header">
      <h2>회원 가입</h2>
    </div>
    <form @submit.prevent="signup" class="signup-form">
      <div class="form-group-row">
        <label for="name">이름</label>
        <input id="name" v-model="name" type="text" placeholder="이름" required />
      </div>
      <div class="form-group-row">
        <label for="username">아이디</label>
        <input id="username" v-model="username" type="text" placeholder="아이디" required />
      </div>
      <div class="form-group-row">
        <label for="password">비밀번호</label>
        <input id="password" v-model="password" type="password" placeholder="비밀번호" required />
      </div>
      <div class="form-group-row">
        <label for="confirmPassword">비밀번호 확인</label>
        <div class="input-container">
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            placeholder="비밀번호 확인"
            required
          />
          <p v-if="!passwordsMatch" class="error-message">비밀번호가 일치하지 않습니다.</p>
        </div>
      </div>
      <div class="form-group-row">
        <label for="email">이메일</label>
        <div class="input-container email-container">
          <input id="email" v-model="email" type="email" placeholder="이메일" @input="validateEmail" required />
          <button type="button" class="email-verify-button" @click="sendVerificationEmail" :disabled="!isEmailValid">인증</button>
          <p v-if="!isEmailValid && email.length > 0" class="error-message">유효한 이메일 주소를 입력해주세요.</p>
        </div>
      </div>
      <div v-if="isVerificationSent" class="form-group-row">
        <label for="verificationCode">인증번호</label>
        <div class="input-container verification-container">
          <input
            id="verificationCode"
            v-model="verificationCode"
            type="text"
            placeholder="인증번호 입력"
            required
          />
          <button type="button" class="email-verify-button" @click="verifyCode">제출</button>
          <p v-if="verificationError" class="error-message">잘못된 인증번호입니다.</p>
          <p v-if="isVerified" class="success-message">인증이 완료되었습니다!</p>
        </div>
      </div>
      <div class="form-group-row">
        <label for="dob">생년월일</label>
        <input id="dob" v-model="dob" type="date" placeholder="생년월일" required />
      </div>
      <div class="form-group-row">
        <label for="gender">성별</label>
        <select id="gender" v-model="gender" required>
          <option value="남성">남성</option>
          <option value="여성">여성</option>
        </select>
      </div>
      <div class="button-group">
        <button type="submit" class="btn-primary" :disabled="!passwordsMatch || !isVerified">확인</button>
        <button type="button" class="btn-secondary" @click="confirmCancel">취소</button>
      </div>
    </form>
    <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import router from "@/router";
import { useUserStore } from "@/store/useUserStore";

const name = ref("");
const username = ref("");
const password = ref("");
const confirmPassword = ref("");
const email = ref("");
const dob = ref("");
const gender = ref("");
const verificationCode = ref("");
const errorMessage = ref("");
const successMessage = ref("");
const verificationError = ref(false);
const isEmailValid = ref(false);
const isVerificationSent = ref(false);
const isVerified = ref(false);
const userStore = useUserStore();

// 비밀번호가 일치하는지 확인하는 computed 속성
const passwordsMatch = computed(() => password.value === confirmPassword.value);

function validateEmail() {
  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/; // 이메일 유효성 검사 정규식
  isEmailValid.value = emailPattern.test(email.value);
}

async function sendVerificationEmail() {
  if (!isEmailValid.value) {
    alert("유효한 이메일을 입력해주세요.");
    return;
  }

  try {
    await userStore.sendVerificationCode(email.value); // email.value를 매개변수로 전달
    isVerificationSent.value = true; // 인증번호 입력 필드 표시
    if (userStore.verificationError) {
      alert("서버 오류: " + userStore.verificationError); // 오류 메시지 표시
    } else {
      alert("이메일 인증 링크가 전송되었습니다.");
    }
  } catch (error) {
    console.error("인증 요청 중 오류 발생:", error);
    alert("인증 요청 실패");
  }
}


async function verifyCode() {
  try {
    await userStore.verifyCode(verificationCode.value);
    isVerified.value = userStore.isVerified;
    verificationError.value = userStore.verificationError;
    if (isVerified.value) {
      alert("인증이 완료되었습니다!");
    } else {
      alert(verificationError.value || "인증 실패");
    }
  } catch (error) {
    console.error("인증 번호 확인 중 오류 발생:", error);
    alert("인증 실패");
  }
}


function signup() {
  if (!passwordsMatch.value) {
    errorMessage.value = "비밀번호가 일치하지 않습니다.";
    successMessage.value = "";
    return;
  }

  if (!isVerified.value) {
    errorMessage.value = "이메일 인증을 완료해주세요.";
    successMessage.value = "";
    return;
  }

  const users = JSON.parse(localStorage.getItem("users") || "[]");
  if (users.some((user) => user.username === username.value)) {
    errorMessage.value = "이미 사용 중인 아이디입니다.";
    successMessage.value = "";
  } else {
    users.push({
      name: name.value,
      username: username.value,
      password: password.value,
      email: email.value,
      dob: dob.value,
      gender: gender.value,
    });
    localStorage.setItem("users", JSON.stringify(users));
    errorMessage.value = "";
    alert("회원가입이 완료되었습니다!");
    setTimeout(() => {
      router.push({ name: "login" });
    }, 2000);
  }
}

function confirmCancel() {
  const confirmCancel = confirm("취소하시겠습니까?");
  if (confirmCancel) {
    router.push({ name: "Main" });
  }
}
</script>

<style scoped>
.signup-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  margin: auto;
}

.signup-header {
  background-color: #043873;
  color: white;
  width: 100%;
  text-align: center;
  padding: 80px 0;
}

.signup-header h2 {
  margin-bottom: 0px;
}

.signup-form {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 800px;
  gap: 15px;
  padding: 60px;
  margin-top: 30px;
}

.form-group-row {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 20px;
}

.form-group-row label {
  width: 200px !important;
  padding: 10px;
  font-weight: bold;
  color: #333;
}

.form-group-row input,
.form-group-row select {
  width: 100%;
  max-width: 580px;
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
}

.input-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: calc(100% - 220px);
}

/* 이메일 input-container를 상대 위치로 설정 */
.email-container,
.verification-container {
  position: relative;
  width: 100%;
}

.email-container input {
  width: calc(100% - 45px); /* 인증 버튼 크기를 고려한 너비 */
  margin-left: 45px;
  box-sizing: border-box; /* 패딩과 border 포함 */
}

/* 인증번호 제출 버튼과 이메일 인증 버튼 공통 */
.email-verify-button {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%); /* 수직 정렬 보정 */
  background-color: #043873;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 5px 10px;
  font-size: 12px;
  cursor: pointer;
  z-index: 1;
}

.email-verify-button:hover {
  background-color: #0056b3;
}

.email-verify-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.error-message {
  color: red;
  font-size: 12px;
  margin-top: 5px;
}

.success-message {
  color: green;
  font-size: 12px;
  text-align: center;
  margin-top: 10px;
}

.button-group {
  display: flex;
  justify-content: center;
}

.btn-primary {
  background-color: #043873;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  margin: 5px;
  cursor: pointer;
}

.btn-primary:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #ccc;
  color: #333;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  margin: 5px;
  cursor: pointer;
}

.btn-secondary:hover {
  background-color: #aaa;
}
</style>