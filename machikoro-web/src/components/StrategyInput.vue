<script setup>
import { computed, reactive, watch } from 'vue';
import StrategySlider from './StrategySlider.vue';

const props = defineProps({
  modelValue: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['update:modelValue']);

const strategy = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});
</script>

<template>
    <div class="strategy-input">
        <h2>Strategy Preferences</h2>
        <StrategySlider
            v-model="strategy.ATTACK_FOCUSED"
            label="Attack Focused"
        />
        <StrategySlider
            v-model="strategy.OPTIMIST"
            label="Optimist"
        />
        <StrategySlider 
            v-model="strategy.SAVER"
            label="Saver"
        />
        <label>
            <input type="checkbox" v-model="strategy.FACTORY_FOCUSED" :true-value="1" :false-value="0"/>
            Factory Focused
        </label>
        <label>
            <input type="checkbox" v-model="strategy.CHEESE_FOCUSED" :true-value="1" :false-value="0"/>
            Cheese Focused
        </label>
        <label>
            <input type="checkbox" v-model="strategy.FRUIT_AND_VEG_FOCUSED" :true-value="1" :false-value="0"/>
            Fruit and Vegetable Focused
        </label>
    </div>    
</template>