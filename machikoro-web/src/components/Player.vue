<script setup>
import Card from './Card.vue';
import Coin from './Coin.vue';
import { landmarkMap } from '@/landmarkMap';
import Landmark from './Landmark.vue';
import { computed } from 'vue';

import { defineProps } from 'vue';
const props = defineProps({
    player: {
        type: Object,
        required: true
    },
    isCurrentPlayer: {
        type: Boolean,
        default: false
    }
});



const computedLandmarks = computed(() => {
    return Object.fromEntries(
        Object.keys(landmarkMap).map(landmark => [
            landmark,
            props.player.landmarks.includes(landmark)
        ])
    );
});
console.log(computedLandmarks.value);
</script>

<template>
    <div class="player" :class="{ 'isCurrentPlayer': isCurrentPlayer }">
        <p class="title is-4">{{ player.name }}<Coin class="is-float-right" :quantity="player.coins"></Coin></p>
        <div class="playerCards">
            <Card v-for="(quantity, card) in player.stock" :key="card" :cardName="card" :quantity="quantity" />
        </div>
        <div class="playerLandmarks">
            <Landmark v-for="(constructed, landmark) in computedLandmarks" :key="landmark" :landmark="landmark" :constructed="constructed" />
        </div>
    </div>
</template>