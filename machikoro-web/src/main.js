import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
// Vuetify setup
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const vuetify = createVuetify({
  components,
  directives,
})

const app = createApp(App).use(vuetify);

app.mount('#app')
