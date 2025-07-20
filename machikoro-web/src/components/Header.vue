<script setup>
import { ref, defineEmits} from 'vue'

const inputCode = ref("");

const emit = defineEmits(["new-game", "open-game"]);

const isActive = ref(false);

defineProps({
    gameCode: {
        type: String,
        required: true
    }
});

const openGame = () => {
  emit('open-game', inputCode.value);
  inputCode.value = '';
};

const newGame = () => {
  emit('new-game');
  inputCode.value = '';
};

const toggleMenu = () => {
  isActive.value = !isActive.value;
};

</script>

<template>
  <header>
    <nav class="navbar" role="navigation" aria-label="main navigation"> 
        <div class="navbar-brand">
            MACHI KORO

        <a role="button"
           class="navbar-burger"
           :class="{ 'is-active': isActive }"
           aria-label="menu"
           aria-expanded="false"
           @click="toggleMenu">
          <span aria-hidden="true"></span>
          <span aria-hidden="true"></span>
          <span aria-hidden="true"></span>
        </a>
      </div>

      <div class="navbar-menu" :class="{ 'is-active': isActive }">
        <div class="navbar-start">
          <div class="navbar-item">
            <button class="button" @click="newGame">New Game</button>
            <input class="input ml-2" type="text" placeholder="Enter game code" v-model="inputCode" />
          </div>
          <div class="navbar-item">
            <button class="button" @click="openGame">Open Game</button>
          </div>
        </div>

        <div class="navbar-end">
          <div class="navbar-item">
            <p v-if="gameCode" class="title is-6">Game Code: {{ gameCode }}</p>
          </div>
        </div>
      </div>
    </nav>
  </header>
</template>