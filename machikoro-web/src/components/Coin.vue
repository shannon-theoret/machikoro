<script setup>
import coinImg from '../assets/coin.png';
import { defineProps } from 'vue';

defineProps({
    quantity: {
        type: Number,
        required: true
    }
})
</script>

<template>
    <div class="coin-container">
        <div class="coin-amount">
          <p class="coin-amount-text">{{quantity}}</p>
        </div>
        <img :src="coinImg" class="coin" />
    </div>
</template>