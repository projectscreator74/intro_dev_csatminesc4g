function checkAuth() {
  const currentUserEmail = localStorage.getItem('studystackUserEmail');
  if (!currentUserEmail) {
    window.location.href = 'login.html';
  }
}

checkAuth();

window.addEventListener('pageshow', (event) => {
  if (event.persisted) {
    checkAuth();
  }
});

document.addEventListener('DOMContentLoaded', () => {
  const sideMenuList = document.querySelector('#side-menu ul');

  if (!sideMenuList) {
    return;
  }

  const logoutItem = document.createElement('li');
  const logoutLink = document.createElement('a');

  logoutLink.href = 'login.html';
  logoutLink.textContent = 'Log Out';
  logoutLink.addEventListener('click', () => {
    localStorage.removeItem('studystackUserEmail');
  });

  logoutItem.appendChild(logoutLink);
  sideMenuList.appendChild(logoutItem);
});