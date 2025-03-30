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
    }
});

</script>

<template>
    <div class="landmark-container">
        <img :src="landmarkMap[landmark]" class="landmark" :class="{ 'notConstructed': !constructed }" />
        <img v-if="!constructed" :src="constructImg" class="construct" />
    </div>
</template>