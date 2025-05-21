<script setup>
import StrategyInput from './StrategyInput.vue';
import StrategySlider from './StrategySlider.vue';
import { computed} from 'vue';

const props = defineProps({
    modelValue: {
        type: Object,
        required: true
    }
});
const emit = defineEmits(["update:modelValue"]);

const player = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});
</script>

<template>
    <div class="player-input">
        <div class="field">
            <label class="label">Player {{ player.number }}'s Name</label>
            <div class="control">
                <input class="input" type="text" v-model="player.name" placeholder="enter name"/>
            </div>
        </div>
        <label class="checkbox">
            <input type="checkbox" v-model="player.isNPC"  :true-value="true" :false-value="false"  />
            Is NPC
        </label>
        <StrategyInput v-if="player.isNPC" v-model="player.strategy"/>
    </div>
</template>
