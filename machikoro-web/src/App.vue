<script setup>
import Header from './components/Header.vue'
import Game from './components/Game.vue'
import api from '@/api/axiosInstance';
import { ref, onMounted } from 'vue'
import NewGame from './components/NewGame.vue';

const game = ref({});
const toStart = ref(false);
const error = ref("");
const gameCode = ref("");

const newGame = () => {
  game.value = {};
  toStart.value = true;
  error.value = "";
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
    isNPC: player.isNPC,
    strategy: JSON.parse(JSON.stringify(player.strategy))
  }));
  await makeApiCall('/newGame', playerData, null);
  gameCode.value = game.value.code;
  toStart.value = false;
}

const openGame = async (code) => {
  if (code) {
    try {
      const response = await api.get('', {
        params: { gameCode: code }
      });
      game.value = response.data;
      gameCode.value = code;
      sessionStorage.setItem("gameCode", gameCode.value);
      toStart.value = false;
      error.value = "";
    } catch (err) {
      error.value = err.response?.data?.message || "An unknown error occurred.";
    }
  } else {
    error.value = "No game code provided!";
  }
}

const roll = async () => {
  try {
    await makeApiCall('/roll', null, { gameCode: gameCode.value });
  } catch (err) {
    error.value = err.response?.data?.message || "An unknown error occurred.";
  }
}

const rollTwoDice = async () => {
  try {
    await makeApiCall('/rollTwoDice', null, { gameCode: gameCode.value });
  } catch (err) {
    error.value = err.response?.data?.message || "An unknown error occurred.";
  }
}

const confirmRoll = async () => {
  try {
    await makeApiCall('/confirmRoll', null, { gameCode: gameCode.value });
  } catch (err) {
    error.value = err.response?.data?.message || "An unknown error occurred.";
  }
}

const steal = async (playerToStealFrom) => {
  try {
    await makeApiCall('/steal', null, { gameCode: gameCode.value, playerNumber: playerToStealFrom });
  } catch (err) {
    error.value = err.response?.data?.message || "An unknown error occurred.";
  }
}

const purchaseCard = async (cardName) => {
  try {
    await makeApiCall('/purchaseCard', null, { gameCode: gameCode.value, card: cardName });
  } catch (err) {
    error.value = err.response?.data?.message || "An unknown error occurred.";
  }
}

const purchaseLandmark = async (landmarkName) => {
  try {
    await makeApiCall('/purchaseLandmark', null, { gameCode: gameCode.value, landmark: landmarkName });
  } catch (err) {
    error.value = err.response?.data?.message || "An unknown error occurred.";
  }
};

const completeTurn = async () => {
  try {
    await makeApiCall('/completeTurn', null, { gameCode: gameCode.value });
  } catch (err) {
    error.value = err.response?.data?.message || "An unknown error occurred.";
  }
}

const makeNPCMove = async () => {
  try {
    await makeApiCall('/makeNPCMove', null, { gameCode: gameCode.value });
  } catch (err) {
    console.log(err);
    error.value = err.response?.data?.message || "An unknown error occurred.";
  }
}

const testStuff = async () => {
  try {
      const response = await api.get('/testStuff', {
        params: { gameCode: gameCode.value }
      });
      game.value = response.data;
      toStart.value = false;
      error.value = "";
    } catch (err) {
      console.log(err);
      error.value = err.response?.data?.message || "An unknown error occurred.";
    }
}

const makeApiCall = async (url, data, params) => {
    try {
        const response = await api.post(url, data, { params });
        game.value = response.data;
        error.value = "";
    } catch (err) {
        error.value = err.response?.data?.message || "An unknown error occurred.";
    }
};

const updateError = (message) => {
  error.value = message;
};

onMounted(() => {
  const storedGameCode = sessionStorage.getItem("gameCode");
  if (storedGameCode) {
    gameCode.value = storedGameCode;
    openGame(storedGameCode); // Automatically open game if gameCode is stored
  }
});

</script>

<template>
  <Header :gameCode="gameCode" @new-game="newGame" @open-game="openGame"/>
  <div class="machikoro">
  <!--<button @click="testStuff">Test Stuff</button>-->
    {{ error }}
    <NewGame v-if="toStart" @begin-game="beginGame"></NewGame>
    <Game 
      v-if="game.step && game.step !== 'SETUP'" 
      :game="game" 
      @roll="roll" 
      @roll-two-dice="rollTwoDice" 
      @confirm-roll="confirmRoll" 
      @steal="steal" 
      @purchase-card="purchaseCard" 
      @purchase-landmark="purchaseLandmark" 
      @complete-turn="completeTurn"
      @make-npc-move="makeNPCMove"
      @error="updateError">
    </Game>
  </div>
</template>
