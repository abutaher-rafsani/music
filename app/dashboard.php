<?php
/**
 * KLIQ Dashboard (cPanel Production Ready)
 * Updated with the approved Kliq brand color system (Cyan primary, Purple/Pink secondary, 1px card top accent, polished surface).
 */
session_start();
require_once __DIR__ . '/config/db.php';

if (!isset($_SESSION['user_id'])) {
    header('Location: index.php');
    exit;
}

$db = Database::main();
$userId = $_SESSION['user_id'];

try {
    $stmt = $db->prepare("SELECT * FROM users WHERE id = ?");
    $stmt->execute([$userId]);
    $user = $stmt->fetch();
    if (!$user) {
        session_destroy();
        header('Location: index.php');
        exit;
    }
} catch (Exception $e) {
    $user = ['name' => $_SESSION['name'], 'username' => $_SESSION['username'], 'balance' => 50.0];
}

$logout = $_GET['logout'] ?? false;
if ($logout) {
    session_destroy();
    header('Location: index.php');
    exit;
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>KLIQ - Dashboard</title>
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
            border: 1px solid rgba(0, 229, 255, 0.15);
            border-radius: 20px;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.05);
            transition: transform 0.25s ease, box-shadow 0.25s ease;
            position: relative;
            overflow: hidden;
        }
        .kliq-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 20px 30px -10px rgba(0, 229, 255, 0.12);
        }
        /* 1px gradient line top accent */
        .kliq-card::before {
            content: '';
            position: absolute;
            top: 0; left: 0; right: 0;
            height: 2px;
            background: linear-gradient(90deg, var(--kliq-cyan), var(--kliq-purple), var(--kliq-pink));
        }

        .kliq-btn-primary {
            background: var(--kliq-cyan);
            color: #0F172A;
            font-weight: 800;
            transition: all 0.2s ease;
        }
        .kliq-btn-primary:hover {
            transform: scale(1.03) translateY(-1px);
            box-shadow: 0 10px 20px -5px rgba(0, 229, 255, 0.4);
        }

        .kliq-btn-gradient {
            background: linear-gradient(135deg, var(--kliq-cyan), var(--kliq-purple), var(--kliq-pink));
            color: #FFFFFF;
            font-weight: 800;
            transition: all 0.2s ease;
        }
        .kliq-btn-gradient:hover {
            transform: scale(1.03) translateY(-1px);
            box-shadow: 0 10px 20px -5px rgba(147, 51, 234, 0.4);
        }

        .media-zoom {
            overflow: hidden;
            border-radius: 16px;
        }
        .media-zoom img {
            transition: transform 0.35s ease;
        }
        .media-zoom:hover img {
            transform: scale(1.06) translateY(-3px);
        }
    </style>
</head>
<body class="min-h-screen flex flex-col">
    <!-- Navbar -->
    <header class="bg-white border-b border-slate-200 sticky top-0 z-50 px-6 py-4 flex items-center justify-between shadow-sm">
        <div class="flex items-center space-x-3">
            <span class="text-2xl font-black text-cyan-600 tracking-tight">KLIQ</span>
            <span class="text-xs bg-cyan-50 text-cyan-700 border border-cyan-200 px-3 py-1 rounded-full font-bold">Cloud Connected</span>
        </div>
        <div class="flex items-center space-x-4">
            <div class="text-right">
                <p class="text-sm font-bold text-slate-900"><?php echo htmlspecialchars($user['name']); ?></p>
                <p class="text-xs text-cyan-600 font-semibold">@<?php echo htmlspecialchars($user['username']); ?></p>
            </div>
            <a href="dashboard.php?logout=1" class="bg-red-50 hover:bg-red-100 text-red-600 px-3.5 py-2 rounded-xl text-xs font-bold transition">Logout</a>
        </div>
    </header>

    <!-- Main Body -->
    <main class="flex-1 max-w-5xl w-full mx-auto p-6 grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Sidebar / Wallet Card -->
        <div class="space-y-6">
            <div class="kliq-card p-6">
                <h3 class="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">My Wallet (bKash Escrow)</h3>
                <div class="text-3xl font-black text-emerald-600">৳ <?php echo number_format($user['balance'] ?? 50.0, 2); ?></div>
                <p class="text-xs text-slate-500 mt-2">Available Cash & Earned Scout Rewards</p>
            </div>

            <div class="kliq-card p-6">
                <h3 class="text-xs font-bold text-slate-500 uppercase tracking-wider mb-3">Quick Navigation</h3>
                <ul class="space-y-2 text-sm font-medium">
                    <li><a href="#" class="block p-3 rounded-xl bg-cyan-50 text-cyan-700 font-bold border border-cyan-100 transition">📰 Unified Feed</a></li>
                    <li><a href="#" class="block p-3 rounded-xl hover:bg-slate-50 text-slate-700 transition">💼 Scout Gigs & Bounties</a></li>
                    <li><a href="#" class="block p-3 rounded-xl hover:bg-slate-50 text-slate-700 transition">🤝 Circles & Communities</a></li>
                    <li><a href="#" class="block p-3 rounded-xl hover:bg-slate-50 text-slate-700 transition">⚙️ Privacy & Security</a></li>
                </ul>
            </div>
        </div>

        <!-- Main Content Feed -->
        <div class="md:col-span-2 space-y-6">
            <div class="kliq-card p-6">
                <h2 class="text-lg font-bold text-slate-900 mb-4">Create Post / Share Update</h2>
                <textarea rows="3" placeholder="What's happening in your circle or community today?" class="w-full bg-slate-50 border border-slate-200 rounded-xl p-4 text-slate-900 text-sm focus:outline-none focus:border-cyan-500 focus:bg-white transition mb-3"></textarea>
                <div class="flex justify-between items-center">
                    <span class="text-xs text-slate-500 font-medium">📷 Add Media via GCS</span>
                    <button class="kliq-btn-gradient px-6 py-2.5 rounded-xl text-sm shadow-md">Publish Post</button>
                </div>
            </div>

            <div class="kliq-card p-6 space-y-4">
                <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 rounded-full bg-cyan-100 text-cyan-700 flex items-center justify-center font-bold">SR</div>
                    <div>
                        <h4 class="text-sm font-bold text-slate-900">Samia Rahman</h4>
                        <p class="text-xs text-slate-500">Dhanmondi Ward 15 • 2 hours ago</p>
                    </div>
                </div>
                <p class="text-sm text-slate-700 font-medium">Just completed the Dhanmondi Superstore audit gig! Earned ৳250 instantly through KLIQ escrow. 🛒✨</p>
                <div class="media-zoom bg-slate-100 h-48 flex items-center justify-center text-slate-400 text-sm font-semibold">
                    <div class="text-center">
                        <p class="text-xs text-slate-500">GCS Media Attachment Preview</p>
                    </div>
                </div>
                <div class="flex space-x-6 text-xs font-bold text-slate-600 pt-3 border-t border-slate-100">
                    <button class="hover:text-cyan-600 transition flex items-center space-x-1"><span>❤️</span> <span>Like (84)</span></button>
                    <button class="hover:text-cyan-600 transition flex items-center space-x-1"><span>💬</span> <span>Comment (16)</span></button>
                    <button class="hover:text-cyan-600 transition flex items-center space-x-1"><span>🪙</span> <span>Tip 50 Coins</span></button>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
