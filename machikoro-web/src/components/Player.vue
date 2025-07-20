<script setup>
import Card from './Card.vue';
import Coin from './Coin.vue';
import { landmarkMap } from '@/landmarkMap';
import Landmark from './Landmark.vue';
import robot from '../assets/robot.png';

import { defineProps, defineEmits, ref, computed, watch } from 'vue';
const props = defineProps({
    player: {
        type: Object,
        required: true
    },
    isCurrentPlayer: {
        type: Boolean,
        default: false
    },
    isBuyStep: {
        type: Boolean,
        default: false
    }
});

const emit = defineEmits(['select-landmark']);

const selectedLandmark = ref(null);

watch(() => props.isBuyStep, (newVal) => {
  if (!newVal) {
    selectedLandmark.value = null;
    emit('select-landmark', null); // Also emit deselection to parent
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

const handleLandmarkClick = (landmark) => {
  if(!props.isCurrentPlayer || computedLandmarks.value[landmark] === true || !props.isBuyStep) {
    return;
  }
  if (selectedLandmark.value === landmark) {
    selectedLandmark.value = null;
    emit('select-landmark', null);
  } else {
    selectedLandmark.value = landmark;
    emit('select-landmark', landmark);
  }
};

</script>

<template>
    <div class="player box" :class="{ 'isCurrentPlayer': isCurrentPlayer }">
        <p class="title is-4">{{ player.name }}<img v-if="player.npc" :src="robot" alt="robot" class="robot"/><Coin class="is-float-right" :quantity="player.coins"></Coin></p>
        <div class="playerCards">
            <Card v-for="(quantity, card) in player.stock" :key="card" :cardName="card" :quantity="quantity" />
        </div>
        <div class="playerLandmarks">
            <Landmark v-for="(constructed, landmark) in computedLandmarks" :key="landmark" :landmark="landmark" :constructed="constructed" :selected="landmark===selectedLandmark" @click="handleLandmarkClick" />
        </div>
    </div>
</template>