<script setup>
import wheatImg from '../assets/Wheat_Field.png';
import ranchImg from '../assets/Ranch.png';
import bakeryImg from '../assets/Bakery.png';
import cafeImg from '../assets/Cafe.png';
import convenienceStoreImg from '../assets/Convenience_Store.png';
import forestImg from '../assets/Forest.png';
import stadiumImg from '../assets/Stadium.png';
import tvStationImg from '../assets/TV_Station.png';
import cheeseFactoryImg from '../assets/Cheese_Factory.png';
import furnitureFactoryImg from '../assets/Furniture_Factory.png';
import mineImg from '../assets/Mine.png';
import familyRestaurantImg from '../assets/Family_Restaurant.png';
import appleOrchardImg from '../assets/Apple_Orchard.png';
import fruitAndVegetableGardenImg from '../assets/Fruit_and_Vegetable_Market.png';
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
    cardName: {
      type: String,
      required: true
    },
    quantity: {
      type: Number,
      required: true
    },
    selected: {
      type: Boolean,
      default: false
    }
  });

const emit = defineEmits(['click']);

const cardImageMap = {
  "WHEAT": { image: wheatImg },
  "RANCH": { image: ranchImg },
  "BAKERY": { image: bakeryImg },
  "CAFE": { image: cafeImg },
  "CONVENIENCE_STORE": { image: convenienceStoreImg },
  "FOREST": { image: forestImg },
  "STADIUM": { image: stadiumImg },
  "TV_STATION": { image: tvStationImg },
  "CHEESE_FACTORY": { image: cheeseFactoryImg },
  "FURNITURE_FACTORY": { image: furnitureFactoryImg },
  "MINE": { image: mineImg },
  "FAMILY_RESTAURANT": { image: familyRestaurantImg },
  "APPLE_ORCHARD": { image: appleOrchardImg, cardTitle: "Apple Orchard", cardDescription: "Gain 3 coins from the bank, on anyone's turn." },
  "FRUIT_AND_VEGETABLE_GARDEN": { image: fruitAndVegetableGardenImg },
  };

const handleClick = () => {
    emit('click', props.cardName);
  };

  </script>

  <template>
    <div class="image-container" @click="handleClick">
      <img :src="cardImageMap[cardName].image" alt="Card Image" class="card-image" :class="{ highlighted: selected }" />
      <span class="badge">{{ quantity }}</span>
    </div>
  </template>