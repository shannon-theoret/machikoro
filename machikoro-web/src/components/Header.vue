<script setup>
import { ref} from 'vue'

const inputCode = ref("");

const emit = defineEmits(["start-game", "open-game"]);

defineProps({
    gameCode: {
        type: String,
        required: true
    }
});


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
                <button class="button" @click="$emit(`start-game`)">New Game</button>
                <input class="input" type="text" placeholder="enter game code" v-model="inputCode"></input>
            </div>
            <div class="navbar-item">
                <button class="button" @click="$emit('open-game', inputCode)">Open Game</button>
            </div>  
            </div>
            <div class="navbar-end">
                <div class="navbar-item">
                    <p class="title is-4">GAME CODE: {{gameCode}} </p>
                </div>
            </div>    
        </div>
        </div>
    </header>
</template>