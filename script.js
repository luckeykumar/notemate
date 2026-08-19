/* =========================================================
   NOTEMATE — script.js
   Full interactive experience for the academic writing service
   ========================================================= */

'use strict';

/* Set this to the n8n Webhook URL after activating the workflow. */
const EMAIL_AGENT_WEBHOOK_URL = 'https://luckkyy.app.n8n.cloud/webhook/notemate-email-agent';
const EMAIL_AGENT_LOG_KEY = 'nm_email_agent_log';

/* ─── State ─── */
const state = {
  user: null,
  currentSlide: 0,
  testimonialAutoPlay: null,
  currentCourseFilter: 'all',
  selectedCourse: '',
};

/* ─── Course DATA ─── */
const courses = [
  // Engineering
  { id: 'btech', title: 'B.Tech', full: 'Bachelor of Technology', category: 'engineering', emoji: '⚙️', badge: 'popular', tags: ['CSE', 'ECE', 'Mech', 'Civil', 'IT'], price: '299' },
  { id: 'be', title: 'BE', full: 'Bachelor of Engineering', category: 'engineering', emoji: '🔩', badge: 'premium', tags: ['EEE', 'Aerospace', 'Chemical'], price: '299' },
  { id: 'mtech', title: 'M.Tech', full: 'Master of Technology', category: 'engineering', emoji: '🛠️', badge: 'premium', tags: ['AI/ML', 'VLSI', 'Power Systems', 'Robotics'], price: '399' },
  { id: 'diploma', title: 'Diploma Engg.', full: 'Diploma in Engineering', category: 'engineering', emoji: '📐', badge: 'new', tags: ['Mechanical', 'Civil', 'Electrical'], price: '199' },

  // Technology
  { id: 'bca', title: 'BCA', full: 'Bachelor of Computer Applications', category: 'technology', emoji: '💻', badge: 'popular', tags: ['Programming', 'DBMS', 'Networking', 'Web'], price: '249' },
  { id: 'mca', title: 'MCA', full: 'Master of Computer Applications', category: 'technology', emoji: '🖥️', badge: 'popular', tags: ['Software Engg.', 'AI', 'Data Science'], price: '349' },
  { id: 'bsc-cs', title: 'B.Sc (CS / IT)', full: 'Bachelor of Science in CS/IT', category: 'technology', emoji: '🔬', badge: 'new', tags: ['OS', 'Algorithms', 'Cybersecurity'], price: '229' },

  // Management
  { id: 'mba', title: 'MBA', full: 'Master of Business Administration', category: 'management', emoji: '👔', badge: 'popular', tags: ['Finance', 'HR', 'Marketing', 'Operations'], price: '399' },
  { id: 'bba', title: 'BBA', full: 'Bachelor of Business Administration', category: 'management', emoji: '📈', badge: 'popular', tags: ['Entrepreneurship', 'Accounting', 'BRM'], price: '249' },
  { id: 'mba-exe', title: 'MBA (Executive)', full: 'Executive MBA', category: 'management', emoji: '🏆', badge: 'premium', tags: ['Corporate', 'Leadership', 'Global Mgmt.'], price: '499' },

  // Science
  { id: 'bsc', title: 'B.Sc', full: 'Bachelor of Science', category: 'science', emoji: '🧪', badge: 'popular', tags: ['Physics', 'Chemistry', 'Maths', 'Bio'], price: '199' },
  { id: 'msc', title: 'M.Sc', full: 'Master of Science', category: 'science', emoji: '🔭', badge: 'premium', tags: ['Biotech', 'Environmental', 'Microbiology'], price: '299' },
  { id: 'bpharm', title: 'B.Pharm / M.Pharm', full: 'Bachelor / Master of Pharmacy', category: 'science', emoji: '💊', badge: 'new', tags: ['Pharmacognosy', 'Medicinal Chem'], price: '299' },

  // Commerce
  { id: 'bcom', title: 'B.Com', full: 'Bachelor of Commerce', category: 'commerce', emoji: '💰', badge: 'popular', tags: ['Accounts', 'Taxation', 'Finance'], price: '199' },
  { id: 'mcom', title: 'M.Com', full: 'Master of Commerce', category: 'commerce', emoji: '📉', badge: 'premium', tags: ['Economics', 'Auditing', 'Business Law'], price: '249' },
  { id: 'bba-fin', title: 'BBA (Finance)', full: 'BBA in Financial Markets', category: 'commerce', emoji: '📊', badge: 'new', tags: ['Stock Market', 'Banking', 'Insurance'], price: '249' },

  // Arts
  { id: 'ba', title: 'BA', full: 'Bachelor of Arts', category: 'arts', emoji: '🎨', badge: 'popular', tags: ['History', 'Political Sci.', 'Sociology', 'English'], price: '149' },
  { id: 'ma', title: 'MA', full: 'Master of Arts', category: 'arts', emoji: '📚', badge: 'premium', tags: ['Psychology', 'Economics', 'Philosophy'], price: '199' },
  { id: 'llb', title: 'LLB / LLM', full: 'Bachelor / Master of Laws', category: 'arts', emoji: '⚖️', badge: 'popular', tags: ['Criminal Law', 'Corporate Law', 'IPR'], price: '399' },
];

/* ─── Testimonial DATA ─── */
const testimonials = [
  { name: 'Aarav Mehta', info: 'B.Tech CSE, VIT Pune', initials: 'AM', rating: 5, quote: 'NOTEMATE completely transformed how I handle assignments. The quality of their notes is outstanding — precise, well-structured, and right on syllabus. Saved me during my exam prep!' },
  { name: 'Priya Sharma', info: 'MBA Marketing, NMIMS Mumbai', initials: 'PS', rating: 5, quote: 'The team truly understands what college students need. My MBA project was delivered 2 days ahead of schedule and my professor was very impressed with the research depth.' },
  { name: 'Rohan Gupta', info: 'MCA, Delhi University', initials: 'RG', rating: 5, quote: 'Fantastic service! Their experts know the exact format required for Delhi University. Revisions were done super fast. Worth every rupee of my Pro plan subscription.' },
  { name: 'Ananya Iyer', info: 'B.Sc Biotech, Bangalore Univ.', initials: 'AI', rating: 5, quote: 'I was stressed about my practical lab manuals. NOTEMATE sorted everything perfectly, with all the diagrams and observations accurately documented. Highly recommended!' },
  { name: 'Karan Patel', info: 'B.Com, Gujarat University', initials: 'KP', rating: 5, quote: 'Very affordable pricing for a student on a budget. The Starter plan gave me exactly what I needed for 3 months. Now upgraded to Pro — no looking back!' },
  { name: 'Sneha Reddy', info: 'LLB, Osmania University', initials: 'SR', rating: 5, quote: 'Legal assignments can be very complex, but NOTEMATE has writers who understand case laws and legal frameworks. My moot court preparation notes were exceptional!' },
];

/* ─────────────────── DOM Ready ─────────────────── */
document.addEventListener('DOMContentLoaded', () => {
  NM_DB.init().then(() => {
    initNavbar();
    renderCourses();
    renderTestimonials();
    initScrollAnimations();
    animateCounters();
    startTestimonialAutoPlay();
    setMinDate();
    restoreSession();
  });
});

/* ─────────────────── NAVBAR ─────────────────── */
function initNavbar() {
  const navbar = document.getElementById('navbar');
  const hamburger = document.getElementById('hamburger');
  const navLinks = document.getElementById('nav-links');
  const accountBtn = document.getElementById('account-btn');
  const accountDropdown = document.getElementById('account-dropdown');

  // Navbar scroll effect
  const onScroll = () => {
    navbar.classList.toggle('scrolled', window.scrollY > 40);
    document.getElementById('scroll-top-btn').classList.toggle('visible', window.scrollY > 400);
  };
  window.addEventListener('scroll', onScroll, { passive: true });

  // Hamburger
  hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('open');
    navLinks.classList.toggle('mobile-open');
  });

  // Close mobile nav on link click
  navLinks.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', () => {
      hamburger.classList.remove('open');
      navLinks.classList.remove('mobile-open');
    });
  });

  // Account dropdown toggle
  accountBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    const isOpen = accountDropdown.classList.toggle('open');
    accountBtn.setAttribute('aria-expanded', isOpen.toString());
  });

  // Close dropdown on outside click
  document.addEventListener('click', (e) => {
    if (!document.getElementById('account-wrapper').contains(e.target)) {
      accountDropdown.classList.remove('open');
      accountBtn.setAttribute('aria-expanded', 'false');
    }
  });

  // Smooth scroll for nav links
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
      const target = document.querySelector(this.getAttribute('href'));
      if (target) {
        e.preventDefault();
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    });
  });
}

/* ─────────────────── SECTION NAVIGATION ─────────────────── */
function scrollToSection(id) {
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: 'smooth' });
}
function openSection(id) {
  closeAllDropdowns();
  scrollToSection(id);
}

/* ─────────────────── COURSES ─────────────────── */
function renderCourses(filter = 'all') {
  const grid = document.getElementById('courses-grid');
  const filtered = filter === 'all' ? courses : courses.filter(c => c.category === filter);

  grid.innerHTML = filtered.map(c => `
    <div class="course-card" data-category="${c.category}" data-id="${c.id}">
      <div class="course-card-header">
        <span class="course-emoji">${c.emoji}</span>
        <span class="course-badge badge-${c.badge}">${c.badge.charAt(0).toUpperCase() + c.badge.slice(1)}</span>
        <h3>${c.title}</h3>
        <p class="course-card-subtitle">${c.full}</p>
        <div class="course-tags">
          ${c.tags.map(t => `<span class="course-tag">${t}</span>`).join('')}
        </div>
      </div>
      <div class="course-card-footer">
        <button class="book-slot-btn" onclick="openBookingModal('${c.title} — ${c.full}')">
          Book a Slot
        </button>
      </div>
    </div>
  `).join('');

  // Animate cards in
  requestAnimationFrame(() => {
    grid.querySelectorAll('.course-card').forEach((card, i) => {
      card.style.opacity = '0';
      card.style.transform = 'translateY(20px)';
      setTimeout(() => {
        card.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
        card.style.opacity = '1';
        card.style.transform = 'translateY(0)';
      }, i * 60);
    });
  });
}

function filterCourses(filter, btn) {
  state.currentCourseFilter = filter;
  document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  renderCourses(filter);
}

/* ─────────────────── TESTIMONIALS ─────────────────── */
function renderTestimonials() {
  const track = document.getElementById('testimonials-track');
          type: document.getElementById('book-type').value,

  track.innerHTML = testimonials.map(t => `
    <div class="testimonial-card">
      <div class="t-stars">
        ${Array(t.rating).fill('<span class="t-star">★</span>').join('')}
      </div>
      <p class="t-quote">"${t.quote}"</p>
      <div class="t-author">
        <div class="t-avatar">${t.initials}</div>
        <div>
          <p class="t-name">${t.name}</p>
          <p class="t-info">${t.info}</p>
        </div>
      </div>
    </div>
  `).join('');

  dotsContainer.innerHTML = Array.from({ length: testimonials.length }, (_, i) =>
    `<button class="dot ${i === 0 ? 'active' : ''}" onclick="goToSlide(${i})" aria-label="Slide ${i + 1}"></button>`
  ).join('');

  updateSlider();
}

function getSlidesVisible() {
  return window.innerWidth >= 1024 ? 3 : window.innerWidth >= 640 ? 2 : 1;
}

function updateSlider() {
  const track = document.getElementById('testimonials-track');
  const cardWidth = track.querySelector('.testimonial-card')?.offsetWidth || 0;
  const gap = 24;
  const offset = state.currentSlide * (cardWidth + gap);
  track.style.transform = `translateX(-${offset}px)`;

  document.querySelectorAll('.dot').forEach((dot, i) => {
    dot.classList.toggle('active', i === state.currentSlide);
  });
}

function slideTestimonials(dir) {
  const max = testimonials.length - getSlidesVisible();
  state.currentSlide = Math.max(0, Math.min(state.currentSlide + dir, max));
  updateSlider();
  resetAutoPlay();
}

function goToSlide(idx) {
  state.currentSlide = idx;
  updateSlider();
  resetAutoPlay();
}

function startTestimonialAutoPlay() {
  state.testimonialAutoPlay = setInterval(() => {
    const max = testimonials.length - getSlidesVisible();
    state.currentSlide = state.currentSlide >= max ? 0 : state.currentSlide + 1;
    updateSlider();
  }, 4000);
}

function resetAutoPlay() {
  clearInterval(state.testimonialAutoPlay);
  startTestimonialAutoPlay();
}

window.addEventListener('resize', () => {
  updateSlider();
});

/* ─────────────────── SCROLL ANIMATIONS ─────────────────── */
function initScrollAnimations() {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
      }
    });
  }, { threshold: 0.1 });

  document.querySelectorAll('.feature-card').forEach(el => observer.observe(el));
}

/* ─────────────────── COUNTER ANIMATION ─────────────────── */
function animateCounters() {
  const counters = document.querySelectorAll('.stat-number[data-target]');
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const el = entry.target;
        const target = parseInt(el.getAttribute('data-target'));
        const duration = 1800;
        const step = target / (duration / 16);
        let current = 0;
        const timer = setInterval(() => {
          current += step;
          if (current >= target) { current = target; clearInterval(timer); }
          el.textContent = Math.floor(current).toLocaleString('en-IN');
        }, 16);
        observer.unobserve(el);
      }
    });
  }, { threshold: 0.5 });
  counters.forEach(c => observer.observe(c));
}

/* ─────────────────── MODALS ─────────────────── */
function openModal(id) {
  closeAllDropdowns();
  const overlay = document.getElementById(id);
  if (overlay) {
    overlay.classList.add('open');
    document.body.style.overflow = 'hidden';
  }
}

function closeModal(id) {
  const overlay = document.getElementById(id);
  if (overlay) {
    overlay.classList.remove('open');
    document.body.style.overflow = '';
  }
}

function closeModalOutside(event, id) {
  if (event.target === event.currentTarget) closeModal(id);
}

function closeAllDropdowns() {
  const dd = document.getElementById('account-dropdown');
  const btn = document.getElementById('account-btn');
  dd.classList.remove('open');
  btn.setAttribute('aria-expanded', 'false');
}

// Keyboard ESC to close modals
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    document.querySelectorAll('.modal-overlay.open').forEach(m => {
      m.classList.remove('open');
      document.body.style.overflow = '';
    });
    closeAllDropdowns();
  }
  if (e.ctrlKey && e.shiftKey && e.key === 'A') {
    e.preventDefault();
    openModal('adminAccessModal');
  }
});

function submitAdminAccess(e) {
  e.preventDefault();
  const pwd = document.getElementById('admin-access-pwd').value;
  if (pwd === 'notemate@2025') {
    closeModal('adminAccessModal');
    localStorage.removeItem('nm_admin_session');
    window.location.href = 'admin/admin.html';
  } else {
    document.getElementById('admin-access-error').classList.remove('hidden');
    document.getElementById('admin-access-pwd').value = '';
  }
}

/* ─────────────────── AUTH ─────────────────── */
function switchAuthTab(tab) {
  const loginSection = document.getElementById('login-form-section');
  const registerSection = document.getElementById('register-form-section');
  const loginTab = document.getElementById('login-tab');
  const registerTab = document.getElementById('register-tab');

  if (tab === 'login') {
    loginSection.classList.remove('hidden');
    registerSection.classList.add('hidden');
    loginTab.classList.add('active');
    registerTab.classList.remove('active');
  } else {
    loginSection.classList.add('hidden');
    registerSection.classList.remove('hidden');
    loginTab.classList.remove('active');
    registerTab.classList.add('active');
  }
}

function loginUser(e) {
  e.preventDefault();
  const email    = document.getElementById('login-email').value.trim();
  const password = document.getElementById('login-password').value;
  if (!email || !password) return;

  const result = NM_DB.Users.verify(email, password);
  if (result.error) { showToast('❌ ' + result.error); return; }

  const { user } = result;
  NM_DB.LoginActivity.log({ userId: user.id, name: user.name, email: user.email, action: 'login' });
  setUserSession(user);
  closeModal('loginModal');
  showToast(`Welcome back, ${user.name.split(' ')[0]}! 🎉`);
}

function registerUser(e) {
  e.preventDefault();
  const first  = document.getElementById('reg-firstname').value.trim();
  const last   = document.getElementById('reg-lastname').value.trim();
  const email  = document.getElementById('reg-email').value.trim();
  const phone  = document.getElementById('reg-phone').value.trim();
  const course = document.getElementById('reg-course').value;
  const password = document.getElementById('reg-password').value;

  if (!first || !email || !phone || !course) return;

  const name = `${first} ${last}`.trim();
  const result = NM_DB.Users.create({ name, email, phone, course, password });
  if (result.error) { showToast('❌ ' + result.error); return; }

  NM_DB.LoginActivity.log({ userId: result.user.id, name, email, action: 'register' });
  setUserSession(result.user);
  closeModal('loginModal');
  showToast(`Account created! Welcome to NOTEMATE, ${first}! 🚀`);
}

function setUserSession(user) {
  state.user = user;
  localStorage.setItem('nm_user', JSON.stringify(user));
  updateUI();
}

function restoreSession() {
  const saved = localStorage.getItem('nm_user');
  if (saved) {
    try {
      state.user = JSON.parse(saved);
      updateUI();
    } catch (err) {
      localStorage.removeItem('nm_user');
    }
  }
}

function updateUI() {
  const u = state.user;
  const guestSection = document.getElementById('dropdown-guest');
  const userSection = document.getElementById('dropdown-user');
  const accountNameDisplay = document.getElementById('account-name-display');
  const avatarCircle = document.getElementById('avatar-circle');

  if (u) {
    guestSection.classList.add('hidden');
    userSection.classList.remove('hidden');

    const initials = u.name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2);
    document.getElementById('user-initials').textContent = initials;
    document.getElementById('user-full-name').textContent = u.name;
    document.getElementById('user-email-display').textContent = u.email;
    accountNameDisplay.textContent = u.name.split(' ')[0];

    avatarCircle.classList.add('logged');
    avatarCircle.innerHTML = `<span style="font-weight:700;font-size:0.78rem;color:#000">${initials}</span>`;
  } else {
    guestSection.classList.remove('hidden');
    userSection.classList.add('hidden');
    accountNameDisplay.textContent = 'Account';
    avatarCircle.classList.remove('logged');
    avatarCircle.innerHTML = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>`;
  }
}

function logoutUser() {
  if (state.user) {
    NM_DB.LoginActivity.log({ userId: state.user.id, name: state.user.name, email: state.user.email, action: 'logout' });
  }
  state.user = null;
  localStorage.removeItem('nm_user');
  updateUI();
  closeAllDropdowns();
  showToast('You have been logged out. See you soon!');
}

/* ─────────────────── BOOKING ─────────────────── */
function openBookingModal(courseName) {
  state.selectedCourse = courseName;
  document.getElementById('booking-course-title').textContent = courseName;
  setMinDate();

  // Pre-fill if user is logged in
  if (state.user) {
    const nameInput = document.getElementById('book-name');
    const emailInput = document.getElementById('book-email');
    if (nameInput && !nameInput.value) nameInput.value = state.user.name;
    if (emailInput && !emailInput.value) emailInput.value = state.user.email;
  }

  openModal('bookingModal');
}

function setMinDate() {
  const dateInput = document.getElementById('book-date');
  if (dateInput) {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    dateInput.min = tomorrow.toISOString().split('T')[0];
  }
}

function submitBooking(e) {
  e.preventDefault();
  const form = e.target;
  const fileInput = document.getElementById('book-files');
  const files = Array.from(fileInput.files);

  const booking = {
    course: state.selectedCourse,
    name: document.getElementById('book-name').value,
    email: document.getElementById('book-email').value,
    phone: document.getElementById('book-phone').value,
    college: document.getElementById('book-college').value,
    subject: document.getElementById('book-subject').value,
    date: document.getElementById('book-date').value,
    type: document.getElementById('book-type').value,
    requirements: document.getElementById('book-requirements').value,
    plan: null,
    userId: state.user?.id || null,
  };

  if (files.length === 0) {
    NM_DB.Bookings.create({ ...booking, files: null });
    notifyEmailAgent({ eventType: 'booking', ...booking, files: [] });
    closeModal('bookingModal');
    showToast(`Thank you, ${booking.name.split(' ')[0]}! We will reach you soon. 📞`);
    form.reset();
    document.getElementById('file-names').textContent = 'No files chosen';
    return;
  }

  // Read all files as base64 then save
  const readers = files.map(file => new Promise(resolve => {
    const reader = new FileReader();
    reader.onload = () => resolve({ name: file.name, type: file.type, data: reader.result });
    reader.readAsDataURL(file);
  }));

  Promise.all(readers).then(encoded => {
    NM_DB.Bookings.create({ ...booking, files: JSON.stringify(encoded) });
    notifyEmailAgent({
      eventType: 'booking',
      ...booking,
      files: encoded.map(file => ({ name: file.name, type: file.type, size: file.data.length }))
    });
    closeModal('bookingModal');
    showToast(`Thank you, ${booking.name.split(' ')[0]}! We will reach you soon. 📞`);
    form.reset();
    document.getElementById('file-names').textContent = 'No files chosen';
  });
}

/* ─────────────────── PAYMENT ─────────────────── */
function selectPayMethod(btn, method) {
  document.querySelectorAll('.pm-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');

  const upiForm = document.getElementById('upi-form');
  upiForm.innerHTML = '';

  if (method === 'upi') {
    upiForm.innerHTML = `
      <div class="cod-info" style="border-color:rgba(79,200,255,0.2);">
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" style="color:#4cf;margin-bottom:8px;">
          <rect x="2" y="5" width="20" height="14" rx="2" stroke="currentColor" stroke-width="2"/>
          <path d="M2 10h20" stroke="currentColor" stroke-width="2"/>
        </svg>
        <p style="font-weight:600;margin-bottom:4px;">Secure UPI Payment</p>
        <p style="font-size:0.85rem;color:#a0a0a0;">Click Pay to open Razorpay — supports UPI, PhonePe, GPay, Paytm and more.</p>
      </div>`;
  } else if (method === 'cod') {
    upiForm.innerHTML = `
      <div class="cod-info">
        <svg width="36" height="36" viewBox="0 0 24 24" fill="none" style="color:#4fc;margin-bottom:8px;">
          <line x1="12" y1="1" x2="12" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <path d="M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
        <p style="font-weight:600;margin-bottom:4px;">Pay when work is delivered</p>
        <p style="font-size:0.85rem;color:#a0a0a0;">Our team will contact you to confirm the order. Payment is collected in cash upon delivery of your completed work.</p>
      </div>`;
  }
}

function processPayment(e) {
  e.preventDefault();
  const isCod = document.querySelector('.pm-btn.active')?.textContent.trim().startsWith('Cash');
  if (isCod) {
    closeModal('paymentModal');
    showToast('Order placed! We will contact you to confirm. Pay on delivery. 💵');
    return;
  }
// api key to implement********************************************************************************************************
  const user = state.user;
  const options = {
    key: 'rzp_test_YourKeyHere',
    amount: 30000,
    currency: 'INR',
    name: 'NOTEMATE',
    description: 'Monthly Subscription',
    image: 'logo/NoteMate Circular.png',
    prefill: {
      name: user?.name || '',
      email: user?.email || '',
      contact: user?.phone || '',
    },
    theme: { color: '#ffffff' },
    handler: function (response) {
      closeModal('paymentModal');
      showToast('Payment of ₹300 successful! Subscription activated! ✅');
    },
    modal: {
      ondismiss: function () {
        showToast('Payment cancelled.');
      }
    }
  };

  const rzp = new Razorpay(options);
  rzp.open();
}

/* ─────────────────── CONTACT ─────────────────── */
function submitContactForm(e) {
  e.preventDefault();
  const contact = {
    name: document.getElementById('cf-name').value,
    email: document.getElementById('cf-email').value,
    course: document.getElementById('cf-course').value,
    message: document.getElementById('cf-message').value,
    userId: state.user?.id || null,
  };
  NM_DB.Contacts.create(contact);
  notifyEmailAgent({ eventType: 'inquiry', ...contact });
  showToast(`Message sent, ${contact.name}! We'll get back to you within 24 hours. 📩`);
  e.target.reset();
}

function notifyEmailAgent(payload) {
  if (!EMAIL_AGENT_WEBHOOK_URL) return;

  const delivery = {
    id: `${Date.now()}-${Math.random().toString(36).slice(2, 7)}`,
    kind: payload.eventType === 'booking' ? 'Booking' : 'Inquiry',
    status: 'Pending',
    createdAt: new Date().toISOString()
  };
  saveEmailAgentDelivery(delivery);

  fetch(EMAIL_AGENT_WEBHOOK_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      ...payload,
      source: 'notemate-web',
      submittedAt: new Date().toISOString()
    }),
    keepalive: true
  }).then(response => {
    updateEmailAgentDelivery(delivery.id, response.ok ? 'Delivered' : 'Failed');
  }).catch(error => {
    updateEmailAgentDelivery(delivery.id, 'Failed');
    console.error('Email agent notification failed:', error);
  });
}

function getEmailAgentDeliveries() {
  try { return JSON.parse(localStorage.getItem(EMAIL_AGENT_LOG_KEY) || '[]'); }
  catch (error) { return []; }
}

function saveEmailAgentDelivery(delivery) {
  const deliveries = [delivery, ...getEmailAgentDeliveries()].slice(0, 50);
  localStorage.setItem(EMAIL_AGENT_LOG_KEY, JSON.stringify(deliveries));
}

function updateEmailAgentDelivery(id, status) {
  const deliveries = getEmailAgentDeliveries().map(delivery =>
    delivery.id === id ? { ...delivery, status } : delivery
  );
  localStorage.setItem(EMAIL_AGENT_LOG_KEY, JSON.stringify(deliveries));
}

function togglePassword(inputId, btn) {
  const input = document.getElementById(inputId);
  const isHidden = input.type === 'password';
  input.type = isHidden ? 'text' : 'password';
  btn.innerHTML = isHidden
    ? `<svg width="18" height="18" viewBox="0 0 24 24" fill="none">
        <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        <path d="M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
      </svg>`
    : `<svg width="18" height="18" viewBox="0 0 24 24" fill="none">
        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
        <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
      </svg>`;
}

/* ─────────────────── SETTINGS ─────────────────── */
function toggleTheme(checkbox) {
  document.body.classList.toggle('light-mode', checkbox.checked);
  localStorage.setItem('nm_theme', checkbox.checked ? 'light' : 'dark');
}

// Restore theme
(function() {
  const theme = localStorage.getItem('nm_theme');
  if (theme === 'light') {
    document.body.classList.add('light-mode');
    // Update toggle when settings modal opens
    document.addEventListener('click', function self(e) {
      const th = localStorage.getItem('nm_theme');
      const toggles = document.querySelectorAll('.toggle-switch input');
      if (toggles.length && th === 'light') {
        toggles[toggles.length - 1].checked = true;
      }
    }, { once: true });
  }
})();

/* ─────────────────── FAQ ─────────────────── */
function toggleFaq(btn) {
  const answer = btn.nextElementSibling;
  const allFaqs = document.querySelectorAll('.faq-q');
  const allAnswers = document.querySelectorAll('.faq-a');

  // Close others
  allFaqs.forEach((q, i) => {
    if (q !== btn) {
      q.classList.remove('open');
      allAnswers[i].classList.remove('open');
    }
  });

  btn.classList.toggle('open');
  answer.classList.toggle('open');
}

/* ─────────────────── TOAST ─────────────────── */
let toastTimer = null;
function showToast(message) {
  const toast = document.getElementById('toast');
  const msg = document.getElementById('toast-message');
  msg.textContent = message;

  toast.classList.remove('show', 'hide');
  void toast.offsetWidth; // Force reflow
  toast.classList.add('show');

  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => {
    toast.classList.add('hide');
    setTimeout(() => toast.classList.remove('show', 'hide'), 400);
  }, 3500);
}

function showFileNames(input) {
  const label = document.getElementById('file-names');
  if (input.files.length === 0) {
    label.textContent = 'No files chosen';
  } else if (input.files.length === 1) {
    label.textContent = input.files[0].name;
  } else {
    label.textContent = `${input.files.length} files selected`;
  }
}

/* ─────────────────── ACTIVE NAV HIGHLIGHT ─────────────────── */
const sections = document.querySelectorAll('section[id]');
const navLinks = document.querySelectorAll('.nav-link');

const sectionObserver = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      navLinks.forEach(link => {
        link.classList.toggle('active-nav', link.getAttribute('href') === `#${entry.target.id}`);
      });
    }
  });
}, { rootMargin: '-40% 0px -55% 0px' });

sections.forEach(s => sectionObserver.observe(s));

// Active nav style
const styleTag = document.createElement('style');
styleTag.textContent = `.nav-link.active-nav { color: var(--text-primary); background: var(--accent-dim); }`;
document.head.appendChild(styleTag);
