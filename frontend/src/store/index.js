import { reactive, ref } from 'vue';

const state = reactive({
  autorefresh: true,
  version: ref("dev"),
  updateAvailable: ref(false),
  updateVersion: ref(""),
  updateLink: ref(""),
  updateReminder: ref(true),
  pollingRate: ref(5000)
});

const methods = {

};

export default {
  state,
  methods
};
