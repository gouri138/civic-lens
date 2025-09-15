// document.getElementById('registerForm').addEventListener('submit', function(e) {
//     e.preventDefault();
//     const name = document.getElementById('registerName').value;
//     const email = document.getElementById('registerEmail').value;
//     const password = document.getElementById('registerPassword').value;

//     fetch('http://localhost:8080/api/users/register', {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify({ name, email, password, role: 'USER' })
//     })
//     .then(response => response.json())
//     .then(data => {
//         alert('Registration successful');
//     })
//     .catch(error => {
//         console.error('Error:', error);
//         alert('Registration failed');
//     });
// });

// document.getElementById('loginForm').addEventListener('submit', function(e) {
//     e.preventDefault();
//     const email = document.getElementById('loginEmail').value;
//     const password = document.getElementById('loginPassword').value;

//     fetch('http://localhost:8080/api/users/login', {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify({ email, password })
//     })
//     .then(response => response.json())
//     .then(data => {
//         if (data.id) {
//             localStorage.setItem('userId', data.id);
//             alert('Login successful');
//             window.location.href = 'complaints.html'; // Redirect to complaints page
//         } else {
//             alert('Login failed');
//         }
//     })
//     .catch(error => {
//         console.error('Error:', error);
//         alert('Login failed');
//     });
// });




document.getElementById('registerForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const name = document.getElementById('registerName').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;

    // Show loading state
    const submitBtn = this.querySelector('.submit-btn');
    const originalContent = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Creating Account...';
    submitBtn.disabled = true;

    fetch('http://localhost:8080/api/users/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ name, email, password, role: 'USER' })
    })
    .then(response => response.json())
    .then(data => {
        showNotification('Registration successful! Please login.', 'success');
        // Clear form and switch to login
        document.getElementById('registerForm').reset();
        showLogin();
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Registration failed. Please try again.', 'error');
    })
    .finally(() => {
        submitBtn.innerHTML = originalContent;
        submitBtn.disabled = false;
    });
});

document.getElementById('loginForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    // Show loading state
    const submitBtn = this.querySelector('.submit-btn');
    const originalContent = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Signing In...';
    submitBtn.disabled = true;

    fetch('http://localhost:8080/api/users/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
    })
    .then(response => response.json())
    .then(data => {
        if (data.id) {
            localStorage.setItem('userId', data.id);
            showNotification('Login successful! Redirecting...', 'success');
            
            // Check if admin (you might want to add role check from backend)
            // For now, redirecting to complaints page for regular users
            setTimeout(() => {
                window.location.href = 'complaints.html';
            }, 1500);
        } else {
            showNotification('Invalid email or password', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Login failed. Please check your connection.', 'error');
    })
    .finally(() => {
        submitBtn.innerHTML = originalContent;
        submitBtn.disabled = false;
    });
});

function showNotification(message, type) {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);
    
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}