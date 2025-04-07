<script setup>
import { defineProps, computed } from 'vue';

const props = defineProps({
    die1: {
        type: Number,
        required: true
    },
    die2: {
        type: Number,
        required: true
    }
});

const die1Src = computed(() => props.die1 > 0 ? new URL(`../assets/dice-${props.die1}.png`, import.meta.url).href : null);
const die2Src = computed(() => props.die2 > 0 ? new URL(`../assets/dice-${props.die2}.png`, import.meta.url).href : null);

</script>

<template>
    <div class="dice-container box">
        <img v-if="die1 > 0" :src="die1Src" class="die" />
        <img v-if="die2 > 0" :src="die2Src" class="die" />
    </div>
</template>