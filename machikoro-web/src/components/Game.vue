<script setup>
import GameStock from './GameStock.vue';
import Player from './Player.vue';
import Dice from './Dice.vue';
import { defineProps } from 'vue';

defineProps({
    game: {
        type: Object,
        required: true
    }
});

</script>

<template>
    <div class="game">
        <Dice :die1="game.die1" :die2="game.die2"></Dice>
        <GameStock :gameStock="game.gameStock" />
        <div class="players">
            <Player v-for="player in game.players" :key="player.number" :player="player" :isCurrentPlayer="player.number == game.currentPlayerNumber" />
        </div>    
    </div>       
</template>