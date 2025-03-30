<script setup>
import Header from './components/Header.vue'
import Game from './components/Game.vue'
import api from '@/api/axiosInstance';
import { ref, computed } from 'vue'
import NewGame from './components/NewGame.vue';

const game = ref({});
const toStart = ref(false);
const randomMessage = ref("Welcome to the game!");
const error = ref("");

const gameCode = computed({
  get: () => game.value?.code || "",
  set: (value) => {
    if (!game.value) {
      game.value = {};
    }
    game.value.code = value;
  }
});

const startGame = () => {
  game.value = {};
  toStart.value = true;
}

const beginGame = async (players) => {
  if (players.length < 2) {
    error.value = "At least two players are required to start the game.";
    return;
  }
  error.value = "";
  const playerData = players.map((player, index) => ({
    playerNumber: index + 1,
    playerName: player.name,
    isNPC: player.isNPC
  }));
  makeApiCall('/newGame', playerData, null);
  toStart.value = false;
}

const openGame = async (code) => {
  if (code) {
    try {
      const response = await api.get('', {
        params: { gameCode: code }
      });
      game.value = response.data;
      error.value = "";
    } catch (err) {
      error.value = err.response?.data?.message || "An unknown error occurred.";
    }
  } else {
    error.value = "No game code provided!";
  }
}

const makeApiCall = async (url, data) => {
    try {
        const response = await api.post(url, data);
        game.value = response.data;
        error.value = "";
    } catch (err) {
        error.value = err.response?.data?.message || "An unknown error occurred.";
    }
};
</script>

<template>
  <Header :gameCode="gameCode" @start-game="startGame" @open-game="openGame"/>
  {{ error }}
  <NewGame v-if="toStart" @begin-game="beginGame"></NewGame>
  <Game v-if="game.step && game.step !== 'SETUP'" :game="game"></Game>
</template>
