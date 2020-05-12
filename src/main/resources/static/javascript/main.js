// const tableFirstName = document.getElementById("firstName");
// const tableLastName = document.getElementById("lastName");
//
// const namesPromise = new Promise((resolve, reject) =>{
//     setTimeout(() =>{
//         resolve(["bartholemeu", "chamberlain"]);
//     }, 3000);
//
//     setTimeout(() => {
//         reject("noname");
//     }, 6000)
// });
//
// namesPromise.then(reponse =>{
//     firstName.innerHTML = reponse[0];
//     lastName.innerHTML = reponse[1];
// }).catch(error =>{
//     console.log(error);
// });

const userData = document.getElementById("p1");
const notificationNumber = document.getElementById("notifNumber");

//to use FETCH functions multiple times, it must be an async function
const getRandomUser = async () =>{
  try{
      const fetchRandomUser = await fetch("http://localhost:8080/api-v1/retrieve");
      return await fetchRandomUser.json();
  } catch (e) {
      console.log(e);
  }
};

const getNewNotifications = async () => {
    try{
        const fetchNotifications = await fetch("http://localhost:8080/api-v1/new-notifs");
        return await fetchNotifications.json();
    } catch (e) {
        console.log(e);
    }
};

document.getElementById("userBtn").addEventListener("click", () =>{
     getRandomUser().then(user => {
        userData.innerHTML = JSON.stringify(user);
    });

    const url = "http://localhost:8080/api-v1/save-notif";
    const notification = {
        head: "Random Access",
        body: "A random user has been accessed"
    };

    const jsonString = JSON.stringify(notification);
    const xhr = new XMLHttpRequest();

    xhr.open("POST", url);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(jsonString);

     // getNewNotifications().then(notification => {
     //    if(notification.length > 0) {
     //        notificationNumber.innerHTML = notification.length;
     //    }
     // });
});


document.getElementById("userBtn").addEventListener("click", () => {
   getNewNotifications().then(notification => {
      if(notification.length > 0){
          notificationNumber.innerHTML = notification.length;
      }
   });
});

// document.getElementById("userBtn").addEventListener("click", () => {
//     const url = "http://localhost:8080/api-v1/save-notif";
//     const notification = {
//        head: "Random Access",
//        body: "A random user has been accessed"
//    };
//
//     const jsonString = JSON.stringify(notification);
//     const xhr = new XMLHttpRequest();
//
//     xhr.open("POST", url);
//     xhr.setRequestHeader("Content-Type", "application/json");
//     xhr.send(jsonString);
//
// });

