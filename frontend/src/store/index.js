import { reactive, ref } from 'vue';

const state = reactive({
  autorefresh: true,
  version: ref("dev"),
  updateAvailable: ref(true),
  updateVersion: ref(""),
  updateLink: ref(""),
  updateReminder: ref(false),
  pollingRate: ref(5000)
});

const methods = {

};

export default {
  state,
  methods
};
