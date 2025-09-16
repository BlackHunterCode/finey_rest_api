document.addEventListener('DOMContentLoaded', function() {
    // Theme Toggle
    const themeToggle = document.getElementById('theme-toggle');
    const prefersDarkScheme = window.matchMedia('(prefers-color-scheme: dark)');
    const currentTheme = localStorage.getItem('theme') || 'light';
    
    // Set initial theme
    if (currentTheme === 'dark' || (!currentTheme && prefersDarkScheme.matches)) {
        document.documentElement.setAttribute('data-theme', 'dark');
        updateThemeIcon('dark');
    } else {
        document.documentElement.setAttribute('data-theme', 'light');
        updateThemeIcon('light');
    }
    
    // Toggle theme on button click
    if (themeToggle) {
        themeToggle.addEventListener('click', function() {
            const currentTheme = document.documentElement.getAttribute('data-theme');
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeIcon(newTheme);
        });
    }
    
    // Update theme icon based on current theme
    function updateThemeIcon(theme) {
        if (!themeToggle) return;
        
        const icon = themeToggle.querySelector('i');
        if (!icon) return;
        
        if (theme === 'dark') {
            icon.className = 'fas fa-sun';
        } else {
            icon.className = 'fas fa-moon';
        }
    }
    
    // Mobile Menu Toggle
    const mobileMenuToggle = document.createElement('button');
    mobileMenuToggle.className = 'mobile-menu-toggle';
    mobileMenuToggle.innerHTML = '<i class="fas fa-bars"></i>';
    mobileMenuToggle.setAttribute('aria-label', 'Menu');
    document.body.appendChild(mobileMenuToggle);
    
    const sidebar = document.querySelector('.sidebar');
    
    mobileMenuToggle.addEventListener('click', function() {
        document.body.classList.toggle('menu-open');
        sidebar.classList.toggle('active');
        
        // Update icon
        const icon = this.querySelector('i');
        if (sidebar.classList.contains('active')) {
            icon.className = 'fas fa-times';
        } else {
            icon.className = 'fas fa-bars';
        }
    });
    
    // Close menu when clicking outside on mobile
    document.addEventListener('click', function(event) {
        if (window.innerWidth <= 768 && !event.target.closest('.sidebar') && !event.target.closest('.mobile-menu-toggle')) {
            sidebar.classList.remove('active');
            document.body.classList.remove('menu-open');
            mobileMenuToggle.querySelector('i').className = 'fas fa-bars';
        }
    });
    
    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            if (targetId === '#') return;
            
            const targetElement = document.querySelector(targetId);
            if (targetElement) {
                window.scrollTo({
                    top: targetElement.offsetTop - 80,
                    behavior: 'smooth'
                });
                
                // Close mobile menu if open
                if (window.innerWidth <= 768) {
                    sidebar.classList.remove('active');
                    document.body.classList.remove('menu-open');
                    mobileMenuToggle.querySelector('i').className = 'fas fa-bars';
                }
            }
        });
    });
    
    // Copy code blocks
    document.querySelectorAll('.code-block').forEach(block => {
        const copyButton = block.querySelector('.copy-btn');
        if (!copyButton) return;
        
        copyButton.addEventListener('click', function() {
            const code = block.querySelector('code');
            if (!code) return;
            
            navigator.clipboard.writeText(code.textContent).then(() => {
                const originalText = this.innerHTML;
                this.innerHTML = '<i class="fas fa-check"></i> Copiado!';
                
                setTimeout(() => {
                    this.innerHTML = originalText;
                }, 2000);
            }).catch(err => {
                console.error('Erro ao copiar o código:', err);
            });
        });
    });
    
    // Handle dropdown menus
    document.querySelectorAll('.nav-section > .section-header').forEach(header => {
        header.addEventListener('click', function(e) {
            e.preventDefault();
            const section = this.parentElement;
            section.classList.toggle('active');
            
            // Toggle chevron icon
            const icon = this.querySelector('.fa-chevron-down');
            if (icon) {
                icon.style.transform = section.classList.contains('active') ? 'rotate(180deg)' : 'rotate(0)';
            }
        });
    });
    
    // Set active menu item based on current page
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    document.querySelectorAll('.sidebar-nav a').forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPage || (currentPage === '' && href === 'index.html')) {
            link.parentElement.classList.add('active');
            
            // Expand parent section if in a submenu
            const parentSection = link.closest('.submenu')?.parentElement;
            if (parentSection) {
                parentSection.classList.add('active');
            }
        }
    });
    
    // Add syntax highlighting (using Prism.js)
    if (typeof Prism !== 'undefined') {
        document.querySelectorAll('pre code').forEach((block) => {
            Prism.highlightElement(block);
        });
    }
});

// Handle browser resize
let resizeTimer;
window.addEventListener('resize', function() {
    clearTimeout(resizeTimer);
    resizeTimer = setTimeout(function() {
        if (window.innerWidth > 768) {
            document.querySelector('.sidebar').classList.remove('active');
            document.body.classList.remove('menu-open');
            const mobileToggle = document.querySelector('.mobile-menu-toggle i');
            if (mobileToggle) {
                mobileToggle.className = 'fas fa-bars';
            }
        }
    }, 250);
});
