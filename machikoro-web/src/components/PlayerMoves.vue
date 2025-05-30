<script setup>
import { defineProps, defineEmits, computed, ref } from 'vue';

const props = defineProps({
    step: {
        type: String,
        required: true
    },
    player: {
        type: Object,
        required: true
    },
    otherPlayers: {
        type: Array,
        required: true
    }
})

defineEmits(['roll','roll-two-dice','confirm-roll','steal','purchase-card','purchase-landmark', 'complete-turn','make-npc-move']);

const npc = computed(() => {
    return (props.player.npc);
})

const playerToStealFrom = ref(props.otherPlayers[0].number);

const canRoll = computed(() => {
    return ((props.step === 'ROLL' || props.step === 'CONFIRM_ROLL'));
});

const canRollTwo = computed(() => {
    return (props.step === 'ROLL' || props.step === 'CONFIRM_ROLL') && props.player.landmarks.includes('TRAIN_STATION');
});

const canConfirmRoll = computed(() => {
    return props.step === 'CONFIRM_ROLL';
});

const canSteal = computed(() => {
    return props.step === 'STEAL';
});

const canPurchaseCardOrLandmark = computed(() => {
    return props.step === 'BUY' && props.player.coins > 0;
});

const canCompleteTurn = computed(() => {
    return props.step === 'BUY';
});

const playerWon = computed(() => {
    return props.step === 'WON';
});

const instructions = computed(() => {
    if (npc.value && canRoll.value) {
        return "It is the roll phase. Click Make NPC Move to have this player choose what to do.";
    } else if (npc.value && canSteal.value) {
        return "TV Station in effect. Click Make NPC Move to have this player choose what to do.";
    } else if (npc.value && canCompleteTurn.value) {
        return "It is the buy phase. Click Make NPC Move to have this player choose what to do."
    } else if (canConfirmRoll.value) {
        if (canRollTwo.value) {
            return "Click Roll or Roll Two Dice to reroll, or Confirm Roll to accept the current roll.";
        } else {
            return "Click Roll to reroll, or Confirm Roll to accept the current roll.";
        }
    } else if (canRollTwo.value) {
        return "Click Roll to roll the die or Roll Two Dice to roll both.";
    } else if (canRoll.value) {
        return "Click Roll to roll the die.";
    } else if (canSteal.value) {
        return "Select a player to steal from to handle the TV Station effect and click Steal."
    } else if (canPurchaseCardOrLandmark.value && canCompleteTurn.value) {
        return "Select a card from the Game Stock to purchase and click Purchase Card, or select a Landmark from your player hand to construct and click Purchase Landmark. If you do not wish to make a purchase this turn click Complete Turn.";
    } else if (props.step === 'BUY' && props.player.coins === 0) {
        return "You cannot afford a card this turn. Click Complete Turn to end your turn.";
    } else if (props.step === 'WON') {
        return "Congratulations!!!!";
    } else {
        return "Invalid step.";
    }
});

</script>

<template>
    <div class="player-moves box">
        <h2 v-if="playerWon" class="title is-4">{{ player.name }} Won</h2>
        <h2 v-else class="title is-4">{{ player.name }}'s Turn</h2>
        <p class="instructions">{{instructions }}</p>
        <div v-if="canSteal" class="select">
            <select v-model="playerToStealFrom">
                <option v-for="otherPlayer in otherPlayers" :key="otherPlayer.number" :value="otherPlayer.number">{{ otherPlayer.name }}</option>
            </select>
        </div>
        <div class="buttons" v-if="npc&&!playerWon">
            <button class="button" @click="$emit('make-npc-move')">Make NPC Move</button>
        </div>
        <div class="buttons" v-else>
            <button v-if="canRoll" class="button" @click="$emit('roll')">Roll</button>
            <button v-if="canRollTwo" class="button" @click="$emit('roll-two-dice')">Roll Two Dice</button>
            <button v-if="canConfirmRoll" class="button" @click="$emit('confirm-roll')">Confirm Roll</button>
            <button v-if="canSteal" class="button" @click="$emit('steal', playerToStealFrom)">Steal</button>
            <button v-if="canPurchaseCardOrLandmark" class="button" @click="$emit('purchase-card')">Purchase Card</button>
            <button v-if="canPurchaseCardOrLandmark" class="button" @click="$emit('purchase-landmark')">Purchase Landmark</button>
            <button v-if="canCompleteTurn" class="button" @click="$emit('complete-turn')">Complete Turn</button>
        </div>
    </div>
</template>