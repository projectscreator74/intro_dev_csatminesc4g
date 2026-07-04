// Sidebar toggle - shared across all pages
const sidebar = document.getElementById('sidebar');
const overlay = document.getElementById('overlay');
const toggleBtn = document.getElementById('sidebar-toggle');
const arrow = toggleBtn.querySelector('.arrow');

function openSidebar() {
  sidebar.classList.add('open');
  overlay.classList.add('visible');
  arrow.classList.add('open');
}

function closeSidebar() {
  sidebar.classList.remove('open');
  overlay.classList.remove('visible');
  arrow.classList.remove('open');
}

toggleBtn.addEventListener('click', () => {
  if (sidebar.classList.contains('open')) {
    closeSidebar();
  } else {
    openSidebar();
  }
});

overlay.addEventListener('click', closeSidebar);