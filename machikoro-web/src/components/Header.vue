<script setup>
import { ref, defineEmits} from 'vue'

const inputCode = ref("");

const emit = defineEmits(["new-game", "open-game"]);

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


</script>

<template>
    <header>
        <div class="navbar">
        <div class="navbar-brand">
            MACHI KORO
        </div>
        <div class="navbar-menu">
            <div class="navbar-start">
            <div class="navbar-item">
                <button class="button" @click="newGame">New Game</button>
                <input class="input" type="text" placeholder="enter game code" v-model="inputCode"></input>
            </div>
            <div class="navbar-item">
                <button class="button" @click="openGame">Open Game</button>
            </div>  
            </div>
            <div class="navbar-end">
                <div class="navbar-item">
                    <p v-if="gameCode" class="title is-4">GAME CODE: {{gameCode}} </p>
                </div>
            </div>    
        </div>
        </div>
    </header>
</template>