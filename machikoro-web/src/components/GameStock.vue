<script setup>
import Card from './Card.vue';
import { defineProps } from 'vue';

defineProps({
    gameStock: {
        type: Object,
        required: true
    }
});
</script>

<template>
    <p class="title is-4 is-pulled-left">Game Stock</p>
    <div class="gameStock">
        <Card v-for="(quantity, card) in gameStock" :key="card" :cardName="card" :quantity="quantity" />
    </div>
</template>