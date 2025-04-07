<script setup>
import { landmarkMap } from '@/landmarkMap';
import { defineProps } from 'vue';
import constructImg from '../assets/construct.png';

defineProps({
    landmark: {
        type: String,
        required: true
    },
    constructed: {
        type: Boolean,
        default: false
    },
    selected: {
        type: Boolean,
        default: false
    }
});

const emit = defineEmits(['click']);

</script>

<template>
    <div class="landmark-container"  @click="$emit('click', landmark)" :class="{'highlighted': selected}">
        <img :src="landmarkMap[landmark]" class="landmark" :class="{ 'notConstructed': !constructed}"/>
        <img v-if="!constructed" :src="constructImg" class="construct" />
    </div>
</template>