<?php
/**
 * KLIQ Login & Registration Portal (cPanel Ready)
 * Updated with the approved Kliq brand color system (Cyan primary, Purple/Pink accents, pristine white surface).
 */
session_start();
require_once __DIR__ . '/config/db.php';

$error = '';
$success = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? 'login';
    $db = Database::main();

    if ($action === 'login') {
        $username = trim($_POST['username'] ?? '');
        $password = $_POST['password'] ?? '';

        if (empty($username) || empty($password)) {
            $error = 'Please enter username/phone and password.';
        } else {
            try {
                $stmt = $db->prepare("SELECT * FROM users WHERE username = ? OR phone = ? OR email = ?");
                $stmt->execute([$username, $username, $username]);
                $user = $stmt->fetch();

                if ($user && password_verify($password, $user['password_hash'])) {
                    session_regenerate_id(true);
                    $_SESSION['user_id'] = $user['id'];
                    $_SESSION['username'] = $user['username'];
                    $_SESSION['name'] = $user['name'];
                    $_SESSION['role'] = $user['role'] ?? 'user';
                    
                    header('Location: dashboard.php');
                    exit;
                } else {
                    $error = 'Invalid credentials. Please check your username or password.';
                }
            } catch (Exception $e) {
                $error = 'Login error: ' . $e->getMessage();
            }
        }
    } elseif ($action === 'register') {
        $name = trim($_POST['name'] ?? '');
        $username = trim($_POST['username'] ?? '');
        $phone = trim($_POST['phone'] ?? '');
        $email = trim($_POST['email'] ?? '');
        $password = $_POST['password'] ?? '';

        if (empty($name) || empty($phone) || empty($password)) {
            $error = 'Name, phone, and password are required.';
        } else {
            try {
                $stmt = $db->prepare("SELECT id FROM users WHERE phone = ? OR username = ?");
                $stmt->execute([$phone, $username]);
                if ($stmt->fetch()) {
                    $error = 'User with this phone or username already exists.';
                } else {
                    $userId = 'usr_' . uniqid();
                    $hash = password_hash($password, PASSWORD_BCRYPT);
                    $now = time() * 1000;

                    $insert = $db->prepare("INSERT INTO users (id, name, username, email, phone, password_hash, role, verified, balance, created_at) VALUES (?, ?, ?, ?, ?, ?, 'user', 0, 50.0, ?)");
                    $insert->execute([$userId, $name, $username ?: 'user_' . rand(1000,9999), $email, $phone, $hash, $now]);

                    $success = 'Registration successful! You can now log in with your credentials.';
                }
            } catch (Exception $e) {
                $error = 'Registration error: ' . $e->getMessage();
            }
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>KLIQ - Login & Registration</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        :root {
            --kliq-cyan: #00E5FF;
            --kliq-purple: #9333EA;
            --kliq-pink: #EC4899;
        }
        body { background-color: #F8FAFC; color: #1E293B; font-family: system-ui, -apple-system, sans-serif; }
        
        .kliq-card {
            background: #FFFFFF;
            border: 1px solid rgba(0, 229, 255, 0.2);
            border-radius: 24px;
            box-shadow: 0 20px 40px -15px rgba(0, 0, 0, 0.08);
            position: relative;
            overflow: hidden;
        }
        .kliq-card::before {
            content: '';
            position: absolute;
            top: 0; left: 0; right: 0;
            height: 2px;
            background: linear-gradient(90deg, var(--kliq-cyan), var(--kliq-purple), var(--kliq-pink));
        }

        .kliq-btn-gradient {
            background: linear-gradient(135deg, var(--kliq-cyan), var(--kliq-purple), var(--kliq-pink));
            color: #FFFFFF;
            font-weight: 800;
            transition: all 0.2s ease;
        }
        .kliq-btn-gradient:hover {
            transform: scale(1.02) translateY(-1px);
            box-shadow: 0 10px 20px -5px rgba(147, 51, 234, 0.4);
        }
    </style>
</head>
<body class="min-h-screen flex items-center justify-center p-4">
    <div class="w-full max-w-md kliq-card p-8">
        <div class="text-center mb-8">
            <h1 class="text-3xl font-black text-cyan-600 tracking-wider">KLIQ</h1>
            <p class="text-xs text-slate-500 font-semibold mt-1">Creator Economy, Scout Gigs & Social Circle</p>
        </div>

        <?php if (!empty($error)): ?>
            <div class="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl mb-6 text-xs font-semibold">
                <?php echo htmlspecialchars($error); ?>
            </div>
        <?php endif; ?>

        <?php if (!empty($success)): ?>
            <div class="bg-emerald-50 border border-emerald-200 text-emerald-700 px-4 py-3 rounded-xl mb-6 text-xs font-semibold">
                <?php echo htmlspecialchars($success); ?>
            </div>
        <?php endif; ?>

        <div class="flex mb-6 bg-slate-100 p-1 rounded-xl">
            <button onclick="switchTab('login')" id="tab-login" class="flex-1 py-2.5 rounded-lg text-xs font-extrabold bg-cyan-500 text-slate-900 shadow-sm transition">Login</button>
            <button onclick="switchTab('register')" id="tab-register" class="flex-1 py-2.5 rounded-lg text-xs font-extrabold text-slate-500 hover:text-slate-900 transition">Register</button>
        </div>

        <!-- Login Form -->
        <form method="POST" id="form-login" class="space-y-4">
            <input type="hidden" name="action" value="login">
            <div>
                <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Username or Phone</label>
                <input type="text" name="username" required class="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-cyan-500 focus:bg-white transition">
            </div>
            <div>
                <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Password</label>
                <input type="password" name="password" required class="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-cyan-500 focus:bg-white transition">
            </div>
            <button type="submit" class="w-full kliq-btn-gradient py-3.5 rounded-xl text-sm shadow-md">Login to KLIQ</button>
        </form>

        <!-- Register Form -->
        <form method="POST" id="form-register" class="space-y-4 hidden">
            <input type="hidden" name="action" value="register">
            <div>
                <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Full Name</label>
                <input type="text" name="name" required class="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-cyan-500 focus:bg-white transition">
            </div>
            <div>
                <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Username</label>
                <input type="text" name="username" required class="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-cyan-500 focus:bg-white transition">
            </div>
            <div>
                <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Mobile Number</label>
                <input type="text" name="phone" required placeholder="+8801..." class="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-cyan-500 focus:bg-white transition">
            </div>
            <div>
                <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Email</label>
                <input type="email" name="email" class="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-cyan-500 focus:bg-white transition">
            </div>
            <div>
                <label class="block text-xs font-bold text-slate-600 uppercase mb-1">Password</label>
                <input type="password" name="password" required class="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-cyan-500 focus:bg-white transition">
            </div>
            <button type="submit" class="w-full kliq-btn-gradient py-3.5 rounded-xl text-sm shadow-md">Create Account & Get ৳50 Bonus</button>
        </form>
    </div>

    <script>
        function switchTab(tab) {
            const loginForm = document.getElementById('form-login');
            const regForm = document.getElementById('form-register');
            const loginTab = document.getElementById('tab-login');
            const regTab = document.getElementById('tab-register');

            if (tab === 'login') {
                loginForm.classList.remove('hidden');
                regForm.classList.add('hidden');
                loginTab.className = 'flex-1 py-2.5 rounded-lg text-xs font-extrabold bg-cyan-500 text-slate-900 shadow-sm transition';
                regTab.className = 'flex-1 py-2.5 rounded-lg text-xs font-extrabold text-slate-500 hover:text-slate-900 transition';
            } else {
                loginForm.classList.add('hidden');
                regForm.classList.remove('hidden');
                regTab.className = 'flex-1 py-2.5 rounded-lg text-xs font-extrabold bg-cyan-500 text-slate-900 shadow-sm transition';
                loginTab.className = 'flex-1 py-2.5 rounded-lg text-xs font-extrabold text-slate-500 hover:text-slate-900 transition';
            }
        }
    </script>
</body>
</html>
