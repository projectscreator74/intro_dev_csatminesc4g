const sideMenu = document.getElementById('side-menu');
const backdrop = document.getElementById('backdrop');
const menuToggle = document.getElementById('menu-toggle');
const arrowIcon = menuToggle.querySelector('.nav-arrow');

function openMenu() {
  sideMenu.classList.add('open');
  backdrop.classList.add('visible');
  arrowIcon.classList.add('open');
}

function closeMenu() {
  sideMenu.classList.remove('open');
  backdrop.classList.remove('visible');
  arrowIcon.classList.remove('open');
}

menuToggle.addEventListener('click', () => {
  sideMenu.classList.contains('open') ? closeMenu() : openMenu();
});

backdrop.addEventListener('click', closeMenu);