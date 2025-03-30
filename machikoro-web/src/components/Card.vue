<script setup>
import wheatImg from '../assets/Wheat_Field.svg';
import ranchImg from '../assets/Ranch.svg';
import bakeryImg from '../assets/Bakery.svg';
import cafeImg from '../assets/Cafe.svg';
import convenienceStoreImg from '../assets/Convenience_Store.svg';
import forestImg from '../assets/Forest.svg';
import stadiumImg from '../assets/Stadium.svg';
import tvStationImg from '../assets/TV_Station.svg';
import cheeseFactoryImg from '../assets/Cheese_Factory.svg';
import furnitureFactoryImg from '../assets/Furniture_Factory.svg';
import mineImg from '../assets/Mine.svg';
import familyRestaurantImg from '../assets/Family_Restaurant.svg';
import appleOrchardImg from '../assets/Apple_Orchard.svg';
import fruitAndVegetableGardenImg from '../assets/Fruit_and_Vegetable_Market.svg';

defineProps({
    cardName: {
      type: String,
      required: true
    },
    quantity: {
      type: Number,
      required: true
    }
  });

const cardImageMap = {
    "WHEAT": wheatImg,
    "RANCH": ranchImg,
    "BAKERY": bakeryImg,
    "CAFE": cafeImg,
    "CONVENIENCE_STORE": convenienceStoreImg,
    "FOREST": forestImg,
    "STADIUM": stadiumImg,
    "TV_STATION": tvStationImg,
    "CHEESE_FACTORY": cheeseFactoryImg,
    "FURNITURE_FACTORY": furnitureFactoryImg,
    "MINE": mineImg,
    "FAMILY_RESTAURANT": familyRestaurantImg,
    "APPLE_ORCHARD": appleOrchardImg,
    "FRUIT_AND_VEGETABLE_GARDEN": fruitAndVegetableGardenImg,
  };

  </script>

  <template>
    <div class="image-container">
      <img :src="cardImageMap[cardName]" alt="Card Image" class="card-image" />
      <span class="badge">{{ quantity }}</span>
    </div>
  </template>