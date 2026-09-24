import React, { useState } from 'react';
import { RegistrationForm, RegisterData, UserProfile } from './RegistrationForm';
import { 
  Phone, 
  Lock, 
  LogIn, 
  UserCheck, 
  Sparkles, 
  ShieldCheck, 
  Zap, 
  MessageSquare, 
  HelpCircle, 
  X, 
  CheckCircle2,
  Award,
  Wallet
} from 'lucide-react';

interface AuthScreenProps {
  onLoginSuccess: (user: UserProfile) => void;
  onRegisterSuccess: (data: RegisterData) => void;
}

export const AuthScreen: React.FC<AuthScreenProps> = ({
  onLoginSuccess,
  onRegisterSuccess
}) => {
  const [activeTab, setActiveTab] = useState<'login' | 'register'>('login');
  
  // Login form states
  const [loginIdentifier, setLoginIdentifier] = useState('');
  const [loginPassword, setLoginPassword] = useState('');
  const [isLoggingIn, setIsLoggingIn] = useState(false);
  const [loginError, setLoginError] = useState<string | null>(null);

  // Forgot password modal state
  const [showForgotModal, setShowForgotModal] = useState(false);
  const [forgotPhone, setForgotPhone] = useState('');
  const [forgotSubmitted, setForgotSubmitted] = useState(false);

  const handleLoginSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!loginIdentifier || !loginPassword) {
      setLoginError('দয়া করে মোবাইল নম্বর/ইউজারনেম এবং পাসওয়ার্ড দিন।');
      return;
    }
    setLoginError(null);
    setIsLoggingIn(true);
    setTimeout(() => {
      setIsLoggingIn(false);
      onLoginSuccess({
        id: 'usr_' + Math.random().toString(36).substring(2, 9),
        phone: loginIdentifier,
        fullName: 'মোহাম্মদ আসিফ রহমান',
        username: loginIdentifier.includes('@') ? loginIdentifier.split('@')[0] : 'asif_rahman',
        cashBalance: 1250.50,
        coinBalance: 4500,
        verified: true,
        role: 'contributor',
        avatarLetter: 'আ'
      });
    }, 1000);
  };

  const handleQuickDemoLogin = (userType: 'contributor' | 'scout') => {
    setIsLoggingIn(true);
    setTimeout(() => {
      setIsLoggingIn(false);
      if (userType === 'contributor') {
        onLoginSuccess({
          id: 'usr_demo_1',
          phone: '01711000001',
          fullName: 'আসিফ রহমান',
          username: 'asif_rahman',
          cashBalance: 1250.50,
          coinBalance: 4500,
          verified: true,
          role: 'contributor',
          avatarLetter: 'আ'
        });
      } else {
        onLoginSuccess({
          id: 'usr_demo_2',
          phone: '01822000002',
          fullName: 'নুসরাত জাহান',
          username: 'nusrat_scout',
          cashBalance: 3420.00,
          coinBalance: 12000,
          verified: true,
          role: 'scout',
          avatarLetter: 'নু'
        });
      }
    }, 800);
  };

  const handleForgotSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!forgotPhone || forgotPhone.length < 10) return;
    setForgotSubmitted(true);
    setTimeout(() => {
      setForgotSubmitted(false);
      setShowForgotModal(false);
      setForgotPhone('');
    }, 2000);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-between font-sans selection:bg-sky-500 selection:text-slate-950 pb-8">
      <div>
        {/* Hero Header & Media */}
        <div className="relative w-full h-44 bg-gradient-to-br from-purple-950 via-slate-900 to-indigo-950 rounded-b-[2rem] overflow-hidden shadow-2xl border-b border-slate-800">
          <video
            src="/login.mp4"
            autoPlay
            loop
            muted
            playsInline
            className="absolute inset-0 w-full h-full object-cover opacity-30 mix-blend-overlay"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-slate-950/40 to-transparent"></div>

          {/* Floating Central Branding Pill */}
          <div className="absolute -bottom-6 left-1/2 -translate-x-1/2 z-20 flex flex-col items-center">
            <div className="w-16 h-16 rounded-2xl bg-slate-900/90 backdrop-blur-md p-2.5 shadow-xl border border-slate-700 flex items-center justify-center">
              <img
                src="/logo.png"
                alt="KliqBD Logo"
                className="w-full h-full object-contain rounded-xl drop-shadow"
                onError={(e) => {
                  // Fallback if logo.png doesn't load
                  (e.target as HTMLElement).style.display = 'none';
                }}
              />
              <span className="absolute font-bold text-sky-400 text-xl font-mono hidden if-img-failed">KB</span>
            </div>
          </div>
        </div>

        {/* Main Content Container */}
        <div className="max-w-md mx-auto px-4 pt-10">
          {/* App Title subtitle */}
          <div className="text-center mb-6">
            <h1 className="text-2xl font-black text-white tracking-tight flex items-center justify-center gap-2">
              ক্লিকবিডি <span className="text-xs px-2 py-0.5 rounded-full bg-sky-500/20 text-sky-400 border border-sky-500/30">ফিনটেক ও গিগ</span>
            </h1>
            <p className="text-xs text-slate-400 mt-1">দক্ষিণ এশিয়ার আধুনিক সোশ্যাল গিগ ও ইনকাম প্ল্যাটফর্ম</p>
          </div>

          {/* Segmented Tab Switcher */}
          <div className="bg-slate-900 p-1 rounded-2xl border border-slate-800 flex mb-6 shadow-inner">
            <button
              type="button"
              onClick={() => setActiveTab('login')}
              className={`flex-1 py-2.5 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-2 ${
                activeTab === 'login'
                  ? 'bg-gradient-to-r from-sky-500 to-indigo-600 text-white shadow-md'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              <LogIn size={15} /> লগইন (Login)
            </button>
            <button
              type="button"
              onClick={() => setActiveTab('register')}
              className={`flex-1 py-2.5 rounded-xl text-xs font-bold transition-all flex items-center justify-center gap-2 ${
                activeTab === 'register'
                  ? 'bg-gradient-to-r from-sky-500 to-indigo-600 text-white shadow-md'
                  : 'text-slate-400 hover:text-white'
              }`}
            >
              <UserCheck size={15} /> রেজিস্ট্রেশন (Sign Up)
            </button>
          </div>

          {/* Active Tab View */}
          {activeTab === 'login' ? (
            <div className="space-y-6 animate-fade-in">
              {/* Login Form Card */}
              <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl">
                <h2 className="text-sm font-bold text-slate-200 mb-4 flex items-center gap-2">
                  <Sparkles size={16} className="text-sky-400" /> অ্যাকাউন্টে প্রবেশ করুন
                </h2>

                {loginError && (
                  <div className="mb-4 p-3 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs">
                    {loginError}
                  </div>
                )}

                <form onSubmit={handleLoginSubmit} className="space-y-4">
                  <div>
                    <label className="text-xs font-semibold text-slate-300 block mb-1">মোবাইল নম্বর / ইউজারনেম</label>
                    <div className="relative">
                      <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                        <Phone size={16} />
                      </span>
                      <input
                        type="text"
                        value={loginIdentifier}
                        onChange={e => setLoginIdentifier(e.target.value)}
                        placeholder="01711000001 বা username"
                        className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-4 text-sm font-mono text-white placeholder-slate-500 focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500"
                      />
                    </div>
                  </div>

                  <div>
                    <div className="flex justify-between items-center mb-1">
                      <label className="text-xs font-semibold text-slate-300">পাসওয়ার্ড</label>
                      <button
                        type="button"
                        onClick={() => setShowForgotModal(true)}
                        className="text-[11px] text-sky-400 hover:underline"
                      >
                        পাসওয়ার্ড ভুলে গেছেন?
                      </button>
                    </div>
                    <div className="relative">
                      <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                        <Lock size={16} />
                      </span>
                      <input
                        type="password"
                        value={loginPassword}
                        onChange={e => setLoginPassword(e.target.value)}
                        placeholder="••••••••"
                        className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-4 text-sm font-mono text-white placeholder-slate-500 focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500"
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    disabled={isLoggingIn}
                    className="w-full py-3 rounded-xl bg-gradient-to-r from-sky-500 to-indigo-600 hover:from-sky-400 hover:to-indigo-500 text-white text-sm font-bold shadow-lg shadow-sky-500/30 flex items-center justify-center gap-2 transition-all active:scale-95 disabled:opacity-50"
                  >
                    {isLoggingIn ? (
                      <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    ) : (
                      <>
                        <LogIn size={16} /> লগইন করুন
                      </>
                    )}
                  </button>
                </form>
              </div>

              {/* 1-Tap Quick Demo Switcher */}
              <div className="bg-slate-900/80 border border-slate-800/80 rounded-2xl p-4">
                <h3 className="text-xs font-semibold text-slate-400 mb-3 flex items-center gap-1.5">
                  <Zap size={14} className="text-amber-400" /> ১-ট্যাপ কুইক ডেমো লগইন (Quick Demo)
                </h3>
                <div className="grid grid-cols-2 gap-3">
                  {/* User 1 */}
                  <button
                    type="button"
                    onClick={() => handleQuickDemoLogin('contributor')}
                    className="p-3 rounded-xl bg-slate-800 hover:bg-slate-750 border border-slate-700 text-left transition-all group active:scale-95 flex flex-col justify-between"
                  >
                    <div>
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-xs font-bold text-white group-hover:text-sky-400">আসিফ রহমান</span>
                        <Award size={13} className="text-sky-400" />
                      </div>
                      <p className="text-[10px] text-slate-400 font-mono">01711000001</p>
                    </div>
                    <div className="mt-2 pt-2 border-t border-slate-700/60 flex items-center justify-between text-[10px]">
                      <span className="text-emerald-400 font-bold">৳ 1,250.50</span>
                      <span className="text-amber-400 font-mono">4500 কয়েন</span>
                    </div>
                  </button>

                  {/* User 2 */}
                  <button
                    type="button"
                    onClick={() => handleQuickDemoLogin('scout')}
                    className="p-3 rounded-xl bg-slate-800 hover:bg-slate-750 border border-slate-700 text-left transition-all group active:scale-95 flex flex-col justify-between"
                  >
                    <div>
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-xs font-bold text-white group-hover:text-sky-400">নুসরাত জাহান</span>
                        <ShieldCheck size={13} className="text-purple-400" />
                      </div>
                      <p className="text-[10px] text-slate-400 font-mono">01822000002</p>
                    </div>
                    <div className="mt-2 pt-2 border-t border-slate-700/60 flex items-center justify-between text-[10px]">
                      <span className="text-emerald-400 font-bold">৳ 3,420.00</span>
                      <span className="text-amber-400 font-mono">12000 কয়েন</span>
                    </div>
                  </button>
                </div>
              </div>
            </div>
          ) : (
            <RegistrationForm
              onRegisterSuccess={onRegisterSuccess}
              onSwitchToLogin={() => setActiveTab('login')}
            />
          )}

          {/* Feature Badges Footer */}
          <div className="grid grid-cols-3 gap-3 mt-8 pt-6 border-t border-slate-800/80 text-center">
            <div className="bg-slate-900/60 p-3 rounded-xl border border-slate-800 flex flex-col items-center">
              <div className="w-8 h-8 rounded-full bg-sky-500/10 text-sky-400 flex items-center justify-center mb-1.5">
                <Zap size={16} />
              </div>
              <span className="text-xs font-semibold text-slate-200">স্কাউট গিগস</span>
              <span className="text-[10px] text-slate-400">টাস্ক ও ইনকাম</span>
            </div>

            <div className="bg-slate-900/60 p-3 rounded-xl border border-slate-800 flex flex-col items-center">
              <div className="w-8 h-8 rounded-full bg-emerald-500/10 text-emerald-400 flex items-center justify-center mb-1.5">
                <Wallet size={16} />
              </div>
              <span className="text-xs font-semibold text-slate-200">ক্যাশআউট</span>
              <span className="text-[10px] text-slate-400">বিকাশ / নগদ</span>
            </div>

            <div className="bg-slate-900/60 p-3 rounded-xl border border-slate-800 flex flex-col items-center">
              <div className="w-8 h-8 rounded-full bg-purple-500/10 text-purple-400 flex items-center justify-center mb-1.5">
                <MessageSquare size={16} />
              </div>
              <span className="text-xs font-semibold text-slate-200">লাইভ চ্যাট</span>
              <span className="text-[10px] text-slate-400">এসক্রো সিকিউরড</span>
            </div>
          </div>
        </div>
      </div>

      {/* Forgot Password Modal */}
      {showForgotModal && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4 animate-fade-in">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-sm p-6 relative shadow-2xl">
            <button
              type="button"
              onClick={() => {
                setShowForgotModal(false);
                setForgotSubmitted(false);
                setForgotPhone('');
              }}
              className="absolute top-4 right-4 text-slate-400 hover:text-white"
            >
              <X size={20} />
            </button>

            {forgotSubmitted ? (
              <div className="text-center py-6">
                <div className="w-14 h-14 bg-emerald-500/20 text-emerald-400 rounded-full flex items-center justify-center mx-auto mb-3 border border-emerald-500/40">
                  <CheckCircle2 size={28} />
                </div>
                <h3 className="text-lg font-bold text-white mb-1">পাসওয়ার্ড রিসেট লিংক পাঠানো হয়েছে!</h3>
                <p className="text-xs text-slate-400">আপনার মোবাইল নম্বরে (SMS) ওটিপি ও রিসেট নির্দেশিকা পাঠানো হয়েছে।</p>
              </div>
            ) : (
              <div>
                <h3 className="text-base font-bold text-white mb-2 flex items-center gap-2">
                  <HelpCircle size={18} className="text-sky-400" /> পাসওয়ার্ড পুনরুদ্ধার
                </h3>
                <p className="text-xs text-slate-400 mb-4">আপনার নিবন্ধিত মোবাইল নম্বরটি দিন:</p>

                <form onSubmit={handleForgotSubmit} className="space-y-4">
                  <div className="relative">
                    <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                      <Phone size={16} />
                    </span>
                    <input
                      type="tel"
                      value={forgotPhone}
                      onChange={e => setForgotPhone(e.target.value.replace(/\D/g, '').slice(0, 11))}
                      placeholder="01711000001"
                      className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-4 text-sm font-mono text-white placeholder-slate-500 focus:outline-none focus:border-sky-500"
                    />
                  </div>

                  <button
                    type="submit"
                    className="w-full py-2.5 rounded-xl bg-sky-500 hover:bg-sky-400 text-slate-950 text-xs font-bold shadow-lg shadow-sky-500/20 transition-all"
                  >
                    ওটিপি (OTP) পাঠান
                  </button>
                </form>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
