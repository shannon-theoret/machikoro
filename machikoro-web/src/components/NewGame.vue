<script setup>
import PlayerInput from './PlayerInput.vue';
import { ref, watch } from 'vue';

const emit = defineEmits(["begin-game"]);
const numberOfPlayers = ref(0);
const players = ref([]);

watch(numberOfPlayers, (newCount) => {
    if (newCount > players.value.length) {
        for (let i = players.value.length + 1; i <= newCount; i++) {
            players.value.push({
                number: i,
                name: '',
                isNPC: false,
                strategy: {
                    ATTACK_FOCUSED: 2,
                    OPTIMIST: 2,
                    SAVER: 2,
                    FRUIT_AND_VEG_FOCUSED: 0,
                    CHEESE_FOCUSED: 0,
                    FACTORY_FOCUSED: 0
                }
            });
        }
    } else {
        players.value.splice(newCount);
    }
});
</script>

<template>
    <div class="new-game">
        <div class="select">
            <select v-model.number="numberOfPlayers">
                <option value="0">Select number of players</option>
                <option value="2">2</option>    
                <option value="3">3</option>
                <option value="4">4</option>
            </select>
        </div>
        <PlayerInput
            v-for="(player, index) in players"
            :key="index"
            v-model="players[index]"
        />
        <div class="control">
            <button class="button" @click="$emit('begin-game', players)">Begin Game</button>
        </div>  
    </div>
</template>
