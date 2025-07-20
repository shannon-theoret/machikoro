<script setup>
import StrategyInput from './StrategyInput.vue';
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
        <label v-if="player.isNPC">
            <input type="checkbox" v-model="player.chooseStrategy" :true-value="true" :false-value="false" />
            Choose Strategy
        </label>
        <StrategyInput v-if="player.isNPC && player.chooseStrategy" v-model="player.strategy"/>
    </div>
</template>
