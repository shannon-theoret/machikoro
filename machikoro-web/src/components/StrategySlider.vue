<template>
	<label>{{label}}</label>
	<v-slider
	  v-model="strategyLevel"
	  :min="0"
	  :max="3"
	  :step="1"
	  :ticks="tickLabels"
	  show-ticks="always"
	  class="strategy-slider"
	/>
  </template>
  
<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: Number,
  label: String,
})

const emit = defineEmits(['update:modelValue'])

const strategyLevel = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const tickLabels = {
    	0: 'Not Used',
    	1: 'Minimal',
    	2: 'Average',
    	3: 'Intense'
  	}
</script>