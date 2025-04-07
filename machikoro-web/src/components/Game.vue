<script setup>
import GameStock from './GameStock.vue';
import Player from './Player.vue';
import Dice from './Dice.vue';
import PlayerMoves from './PlayerMoves.vue';
import { defineProps, computed, ref } from 'vue';

const props = defineProps({
    game: {
        type: Object,
        required: true
    }
});

const emit = defineEmits(['roll','roll-two-dice','confirm-roll','steal','purchase-card','purchase-landmark', 'complete-turn', 'error']);

const selectedCard = ref(null);

const selectedLandmark = ref(null);

const sortedPlayers = computed(() => {
  return [...props.game.players].sort((a, b) => a.number - b.number);
});

const otherPlayers = computed(() => {
  return props.game.players.filter(player => player.number !== props.game.currentPlayerNumber);
});

const handleSelectCard = (cardName) => {
    selectedCard.value = cardName;
};

const handleSelectLandmark = (landmark) => {
    selectedLandmark.value = landmark;
};

const handlePurchaseCard = () => {
    if (!selectedCard.value) {
        emit('error', "No card selected for purchase.");
        return;
    } else {
        emit('purchase-card', selectedCard.value);
    }
};

const handlePurchaseLandmark = () => {
    if (!selectedLandmark.value) {
        emit('error', "No landmark selected for purchase.");
        return;
    } else {
        emit('purchase-landmark', selectedLandmark.value);
    }
};

const isBuyStep = computed(() => {
    return props.game.step === 'BUY';
});

</script>

<template>
    <div class="game">
        <div class="top-info">
            <PlayerMoves 
            :step="game.step" 
            :player="sortedPlayers[game.currentPlayerNumber - 1]" 
            :otherPlayers="otherPlayers" 
            @roll="emit('roll')" 
            @roll-two-dice="emit('roll-two-dice')" 
            @confirm-roll="emit('confirm-roll')" 
            @steal="emit('steal', playerToStealFrom)"
            @purchase-card="handlePurchaseCard" 
            @purchase-landmark="handlePurchaseLandmark" 
            @complete-turn="emit('complete-turn')">
            </PlayerMoves>
            <Dice :die1="game.die1" :die2="game.die2"></Dice>
        </div>
        <GameStock :gameStock="game.gameStock" @select-card="handleSelectCard" :isBuyStep="isBuyStep"/>
        <div class="players">
            <Player v-for="player in sortedPlayers" @select-landmark="handleSelectLandmark" :key="player.number" :player="player" :isCurrentPlayer="player.number == game.currentPlayerNumber" :isBuyStep="isBuyStep" />
        </div>    
    </div>       
</template>