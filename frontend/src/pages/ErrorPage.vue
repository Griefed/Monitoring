<template>
  <q-page>
    <div class="fullscreen text-white text-center q-pa-md flex flex-center">
      <div>

        <q-icon
          class="rainbow"
          name="mdi-emoticon-poop-outline"
          size="500px"
        />

        <div class="text-weight-bolder caption">
          Well then... This is awkward...
        </div>

        <div class="text-h1 text-weight-bolder subtext">
          You shouldn't be here...
        </div>

        <q-btn
          class="q-mt-xl"
          color="white"
          label="Go Home"
          no-caps
          size="lg"
          text-color="blue"
          to="/"
          unelevated
        />

      </div>
    </div>
  </q-page>
</template>

<script>
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'ErrorPage'
})
</script>

<style>
.rainbow {
  animation: rainbow 2.5s linear;
  animation-iteration-count: infinite;
}

.caption {
  font-size: 60px;
}

.subtext {
  opacity: 0.4;
}

@keyframes rainbow {
  100%, 0% {
    color: rgb(255, 0, 0);
  }
  8% {
    color: rgb(255, 127, 0);
  }
  16% {
    color: rgb(255, 255, 0);
  }
  25% {
    color: rgb(127, 255, 0);
  }
  33% {
    color: rgb(0, 255, 0);
  }
  41% {
    color: rgb(0, 255, 127);
  }
  50% {
    color: rgb(0, 255, 255);
  }
  58% {
    color: rgb(0, 127, 255);
  }
  66% {
    color: rgb(0, 0, 255);
  }
  75% {
    color: rgb(127, 0, 255);
  }
  83% {
    color: rgb(255, 0, 255);
  }
  91% {
    color: rgb(255, 0, 127);
  }
}
</style>
