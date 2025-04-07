<script setup>
import Card from './Card.vue';
import { defineProps, defineEmits, ref, watch } from 'vue';

const props = defineProps({
    gameStock: {
        type: Object,
        required: true
    },
    isBuyStep: {
        type: Boolean,
        default: false
    }
});

const emit = defineEmits(['select-card']);

const selectedCard = ref(null);

const isOpen = ref(props.isBuyStep);

const toggleOpen = () => {
  isOpen.value = !isOpen.value;
};

watch(() => props.isBuyStep, (newVal) => {
  isOpen.value = newVal;
  if (!newVal) {
    selectedCard.value = null;
    emit('select-card', null);
  }
});

watch(() => props.isBuyStep, (newVal) => {
  if (!newVal) {
    selectedCard.value = null;
    emit('select-card', null);
  }
});

const handleCardClick = (cardName) => {
  if (!props.isBuyStep) return; // Only allow selection during buy step
  if (selectedCard.value === cardName) {
    selectedCard.value = null;
    emit('select-card', null); // deselect
  } else {
    selectedCard.value = cardName;
    emit('select-card', cardName);
  }
};

</script>

<template>
    <div class="box game-stock-container">
        <p class="title is-4 is-pulled-left">Game Stock</p>
        <span class="arrow-icon" @click="toggleOpen">
          {{ isOpen ? '▶' : '▼' }}
        </span>
        <div v-show="isOpen" class="gameStock">
            <Card v-for="(quantity, card) in gameStock" :key="card" :cardName="card" :quantity="quantity" @click="handleCardClick" :selected="card === selectedCard"/>
        </div>
    </div>
    </template>