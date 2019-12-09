import {SendNotification} from '../rest/entities-rest-pagination.js'
const sendNotification = new SendNotification();
async function init() {

  const registration = await navigator.serviceWorker.register('sw.js');
  await navigator.serviceWorker.ready;
  firebase.initializeApp({
    messagingSenderId: "971591344559"
  });
  const messaging = firebase.messaging();
  messaging.usePublicVapidKey('BHHyOm4qTKNKcut1puYM5XMyMR9qxRIVSRAiYx8n2j6R1_QCgb3cuHd1D4QGrqkzl0LYPNwdfEHoluhnu6T8znU');
  messaging.useServiceWorker(registration);

  try {
    await messaging.requestPermission();
  } catch (e) {
    console.log('Unable to get permission', e);
    return;
  }

    const currentToken = await messaging.getToken();
    sendNotification.register(currentToken)

    messaging.onTokenRefresh(async () => {
        console.log('token refreshed');
        sendNotification.register(currentToken)
    });
}

init();
