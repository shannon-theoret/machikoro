<script setup>
defineProps({
    modelValue: {
        type: Object,
        required: true
    }
});
defineEmits(["update:modelValue"]);
</script>

<template>
    <div class="field">
        <label class="label">Player {{ modelValue.number }}'s Name</label>
        <div class="control">
            <input class="input" type="text" v-model="modelValue.name" placeholder="enter name"/>
        </div>
    </div>
    <label class="checkbox">
        <input type="checkbox" v-model="modelValue.isNPC"  :true-value="true" :false-value="false"  />
        Is NPC {{modelValue.isNPC }}
    </label>
</template>
