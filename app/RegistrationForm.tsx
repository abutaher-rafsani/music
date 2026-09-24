import React, { useState, useEffect } from 'react';
import { 
  User, 
  AlternateEmail, 
  Phone, 
  Email, 
  Lock, 
  CardGiftcard, 
  Briefcase, 
  GraduationCap, 
  Calendar, 
  Users, 
  Camera, 
  CheckCircle2, 
  XCircle, 
  ArrowRight, 
  ArrowLeft, 
  Check, 
  ShieldCheck,
  Building,
  MapPin
} from 'lucide-react';

export interface AddressInfo {
  division: string;
  district: string;
  upazila?: string;
  unionCity?: string;
  villageWard?: string;
  details?: string;
}

export interface RegisterData {
  fullName: string;
  username: string;
  phone: string;
  email?: string;
  password: string;
  confirmPassword: string;
  presentAddress: AddressInfo;
  permanentAddress: AddressInfo;
  sameAddress: boolean;
  jobInfo?: string;
  university?: string;
  college?: string;
  school?: string;
  dob?: string;
  gender?: string;
  interests?: string;
  avatarUrl?: string;
  bio?: string;
  userType: string;
  contactSyncEnabled: boolean;
  refCode?: string;
  acceptedTerms: boolean;
}

const BD_DIVISIONS: Record<string, string[]> = {
  "ঢাকা": ["ঢাকা", "গাজীপুর", "নারায়ণগঞ্জ", "টঙ্গী", "সাভার", "মানিকগঞ্জ", "মুন্সিগঞ্জ", "নরসিংদী", "ফরিদপুর", "রাজবাড়ী", "মাদারীপুর", "শরীয়তপুর", "গোপালগঞ্জ"],
  "চট্টগ্রাম": ["চট্টগ্রাম", "কক্সবাজার", "কুমিল্লা", "ব্রাহ্মণবাড়িয়া", "চাঁদপুর", "লক্ষ্মীপুর", "নোয়াখালী", "ফেনী", "বান্দরবান", "খাগড়াছড়ি", "রাঙ্গামাটি"],
  "রাজশাহী": ["রাজশাহী", "বগুড়া", "পাবনা", "সিরাজগঞ্জ", "নওগাঁ", "নাটোর", "চাঁপাইনবাবগঞ্জ", "জয়পুরহাট"],
  "খুলনা": ["খুলনা", "যশোর", "সাতক্ষীরা", "কুষ্টিয়া", "চুয়াডাঙ্গা", "ঝিনাইদহ", "মাগুরা", "নড়াইল", "বাগেরহাট", "মেহেরপুর"],
  "বরিশাল": ["বরিশাল", "ভোলা", "পটুয়াখালী", "পিরোজপুর", "ঝালকাঠি", "বরগুনা"],
  "সিলেট": ["সিলেট", "মৌলভীবাজার", "হবিগঞ্জ", "সুনামগঞ্জ"],
  "রংপুর": ["রংপুর", "দিনাজপুর", "বগুড়া", "কুড়িগ্রাম", "লালমনিরহাট", "নীলফামারী", "পঞ্চগড়", "ঠাকুরগাঁও"],
  "ময়মনসিংহ": ["ময়মনসিংহ", "জামালপুর", "নেত্রকোনা", "শেরপুর"]
};

interface RegistrationFormProps {
  onRegisterSuccess: (data: RegisterData) => void;
  onSwitchToLogin: () => void;
}

export const RegistrationForm: React.FC<RegistrationFormProps> = ({
  onRegisterSuccess,
  onSwitchToLogin
}) => {
  const [step, setStep] = useState<number>(1);
  const [formData, setFormData] = useState<RegisterData>({
    fullName: '',
    username: '',
    phone: '',
    email: '',
    password: '',
    confirmPassword: '',
    presentAddress: { division: 'ঢাকা', district: 'ঢাকা', upazila: '', unionCity: '', villageWard: '', details: '' },
    permanentAddress: { division: 'ঢাকা', district: 'ঢাকা', upazila: '', unionCity: '', villageWard: '', details: '' },
    sameAddress: true,
    jobInfo: '',
    university: '',
    college: '',
    school: '',
    dob: '',
    gender: 'ছাত্র / শিক্ষার্থী',
    interests: 'টেক, ফ্রিল্যান্সিং, সোশ্যাল মিডিয়া',
    avatarUrl: '',
    bio: '',
    userType: 'ছাত্র / শিক্ষার্থী',
    contactSyncEnabled: true,
    refCode: '',
    acceptedTerms: false
  });

  const [usernameStatus, setUsernameStatus] = useState<'idle' | 'checking' | 'available' | 'taken'>('idle');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [showSuccessToast, setShowSuccessToast] = useState<boolean>(false);

  // Debounced username check
  useEffect(() => {
    if (formData.username.length < 3) {
      setUsernameStatus('idle');
      return;
    }
    setUsernameStatus('checking');
    const timer = setTimeout(() => {
      // Mock availability check
      if (['admin', 'root', 'kliq', 'test', 'md_asif'].includes(formData.username.toLowerCase())) {
        setUsernameStatus('taken');
      } else {
        setUsernameStatus('available');
      }
    }, 500);
    return () => clearTimeout(timer);
  }, [formData.username]);

  const handleInputChange = (field: keyof RegisterData, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    setErrorMessage(null);
  };

  const handlePresentAddressChange = (field: keyof AddressInfo, value: string) => {
    setFormData(prev => {
      const updatedPresent = { ...prev.presentAddress, [field]: value };
      // If division changes, reset district to first of new division
      if (field === 'division') {
        const firstDist = BD_DIVISIONS[value]?.[0] || '';
        updatedPresent.district = firstDist;
      }
      return {
        ...prev,
        presentAddress: updatedPresent,
        permanentAddress: prev.sameAddress ? updatedPresent : prev.permanentAddress
      };
    });
  };

  const handlePermanentAddressChange = (field: keyof AddressInfo, value: string) => {
    setFormData(prev => {
      const updatedPermanent = { ...prev.permanentAddress, [field]: value };
      if (field === 'division') {
        const firstDist = BD_DIVISIONS[value]?.[0] || '';
        updatedPermanent.district = firstDist;
      }
      return {
        ...prev,
        permanentAddress: updatedPermanent
      };
    });
  };

  const handleQuickSubmit = () => {
    if (!formData.fullName || !formData.username || !formData.phone || formData.password.length < 6) {
      setErrorMessage('দয়া করে বাধ্যতামূলক ক্ষেত্রগুলি পূরণ করুন (পাসওয়ার্ড কমপক্ষে ৬ অক্ষর)।');
      return;
    }
    const finalData = { ...formData, acceptedTerms: true };
    setShowSuccessToast(true);
    setTimeout(() => {
      onRegisterSuccess(finalData);
    }, 1200);
  };

  const handleNextStep = () => {
    if (step === 1) {
      if (!formData.fullName.trim()) {
        setErrorMessage('পূর্ণ নাম আবশ্যক।');
        return;
      }
      if (formData.username.length < 3 || usernameStatus === 'taken') {
        setErrorMessage('সঠিক এবং ইউনিক ইউজারনেম দিন (কমপক্ষে ৩ অক্ষর)।');
        return;
      }
      if (!formData.phone || formData.phone.length < 10) {
        setErrorMessage('সঠিক মোবাইল নম্বর দিন (কমপক্ষে ১০ ডিজিট)।');
        return;
      }
      if (formData.password.length < 6) {
        setErrorMessage('পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে।');
        return;
      }
      if (formData.password !== formData.confirmPassword) {
        setErrorMessage('পাসওয়ার্ড ও কনফার্ম পাসওয়ার্ড মিলছে না।');
        return;
      }
    }
    setErrorMessage(null);
    setStep(prev => Math.min(prev + 1, 4));
  };

  const handlePrevStep = () => {
    setErrorMessage(null);
    setStep(prev => Math.max(prev - 1, 1));
  };

  const handleFinalSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.acceptedTerms) {
      setErrorMessage('শর্তাবলীতে সম্মত হওয়া বাধ্যতামূলক।');
      return;
    }
    setShowSuccessToast(true);
    setTimeout(() => {
      onRegisterSuccess(formData);
    }, 1500);
  };

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // Simulate client-side compression & preview URL creation
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData(prev => ({ ...prev, avatarUrl: reader.result as string }));
      };
      reader.readAsDataURL(file);
    }
  };

  return (
    <div className="w-full max-w-md mx-auto bg-slate-900 text-slate-100 rounded-2xl shadow-2xl border border-slate-800 p-6 relative overflow-hidden">
      {/* Success Toast Overlay */}
      {showSuccessToast && (
        <div className="absolute inset-0 bg-slate-950/90 z-50 flex flex-col items-center justify-center p-6 text-center animate-fade-in">
          <div className="w-16 h-16 bg-emerald-500/20 text-emerald-400 rounded-full flex items-center justify-center mb-4 border border-emerald-500/40 animate-bounce">
            <CheckCircle2 size={36} />
          </div>
          <h3 className="text-xl font-bold text-white mb-2">রেজিস্ট্রেশন সফল হয়েছে!</h3>
          <p className="text-emerald-400 text-sm mb-4">আপনার অ্যাকাউন্টে ৫০ টাকা সাইন-আপ বোনাস যোগ করা হয়েছে।</p>
          <div className="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin"></div>
        </div>
      )}

      {/* Header & Progress Indicator */}
      <div className="mb-6">
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-sky-400 animate-pulse"></span>
            ক্লিকবিডি রেজিস্ট্রেশন
          </h2>
          <span className="text-xs font-mono px-2.5 py-1 rounded-full bg-slate-800 text-sky-400 border border-slate-700">
            ধাপ {step} / ৪
          </span>
        </div>

        {/* Progress Bar */}
        <div className="w-full bg-slate-800 h-2 rounded-full overflow-hidden">
          <div 
            className="bg-gradient-to-r from-sky-500 to-indigo-500 h-full transition-all duration-300"
            style={{ width: `${(step / 4) * 100}%` }}
          ></div>
        </div>

        {/* Step Titles */}
        <div className="flex justify-between text-[11px] text-slate-400 mt-2 font-medium">
          <span className={step >= 1 ? 'text-sky-400 font-semibold' : ''}>১. তথ্য</span>
          <span className={step >= 2 ? 'text-sky-400 font-semibold' : ''}>২. ঠিকানা</span>
          <span className={step >= 3 ? 'text-sky-400 font-semibold' : ''}>৩. পেশা</span>
          <span className={step >= 4 ? 'text-sky-400 font-semibold' : ''}>৪. ছবি ও শর্ত</span>
        </div>
      </div>

      {/* Error message banner */}
      {errorMessage && (
        <div className="mb-4 p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs flex items-center gap-2">
          <div className="w-1.5 h-1.5 rounded-full bg-rose-500 flex-shrink-0"></div>
          <span>{errorMessage}</span>
        </div>
      )}

      <form onSubmit={handleFinalSubmit} className="space-y-4">
        {/* STEP 1: Mandatory Credentials */}
        {step === 1 && (
          <div className="space-y-3.5 animate-fade-in">
            <div className="flex justify-between items-center">
              <label className="text-xs font-semibold text-slate-300">১.১ পূর্ণ নাম (Full Name)</label>
              <span className="text-[10px] text-rose-400">*আবশ্যক</span>
            </div>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                <User size={16} />
              </span>
              <input
                type="text"
                value={formData.fullName}
                onChange={e => handleInputChange('fullName', e.target.value)}
                placeholder="যেমন: মোহাম্মদ আসিফ রহমান"
                className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-4 text-sm text-white placeholder-slate-500 focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 transition-all font-sans"
              />
            </div>

            <div className="flex justify-between items-center">
              <label className="text-xs font-semibold text-slate-300">১.২ ইউজারনেম (Username)</label>
              <span className="text-[10px] text-slate-400 font-mono">অফিসিয়াল আইডি</span>
            </div>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                <AlternateEmail size={16} />
              </span>
              <input
                type="text"
                value={formData.username}
                onChange={e => handleInputChange('username', e.target.value.toLowerCase().replace(/[^a-z0-9_]/g, ''))}
                placeholder="asif_2026"
                className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-10 text-sm font-mono text-white placeholder-slate-500 focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 transition-all"
              />
              <span className="absolute inset-y-0 right-0 flex items-center pr-3">
                {usernameStatus === 'checking' && (
                  <div className="w-4 h-4 border-2 border-sky-400 border-t-transparent rounded-full animate-spin"></div>
                )}
                {usernameStatus === 'available' && <CheckCircle2 size={16} className="text-emerald-400" />}
                {usernameStatus === 'taken' && <XCircle size={16} className="text-rose-400" />}
              </span>
            </div>
            {usernameStatus === 'taken' && (
              <p className="text-[11px] text-rose-400 mt-[-2px]">এই ইউজারনেমটি ইতিমধ্যে ব্যবহৃত হচ্ছে।</p>
            )}
            {usernameStatus === 'available' && (
              <p className="text-[11px] text-emerald-400 mt-[-2px]">ইউজারনেমটি খালি আছে!</p>
            )}

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">১.৩ মোবাইল নম্বর (Phone)</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <Phone size={16} />
                </span>
                <input
                  type="tel"
                  value={formData.phone}
                  onChange={e => handleInputChange('phone', e.target.value.replace(/\D/g, '').slice(0, 11))}
                  placeholder="01711000001"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-4 text-sm font-mono text-white placeholder-slate-500 focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 transition-all"
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">১.৪ ইমেইল (ঐচ্ছিক)</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <Email size={16} />
                </span>
                <input
                  type="email"
                  value={formData.email}
                  onChange={e => handleInputChange('email', e.target.value)}
                  placeholder="asif@gmail.com"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-4 text-sm text-white placeholder-slate-500 focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 transition-all"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">পাসওয়ার্ড</label>
                <div className="relative">
                  <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                    <Lock size={15} />
                  </span>
                  <input
                    type="password"
                    value={formData.password}
                    onChange={e => handleInputChange('password', e.target.value)}
                    placeholder="কমপক্ষে ৬ অক্ষর"
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-9 pr-3 text-xs font-mono text-white placeholder-slate-500 focus:outline-none focus:border-sky-500"
                  />
                </div>
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">কনফার্ম পাসওয়ার্ড</label>
                <div className="relative">
                  <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                    <Lock size={15} />
                  </span>
                  <input
                    type="password"
                    value={formData.confirmPassword}
                    onChange={e => handleInputChange('confirmPassword', e.target.value)}
                    placeholder="পুনরায় দিন"
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-9 pr-3 text-xs font-mono text-white placeholder-slate-500 focus:outline-none focus:border-sky-500"
                  />
                </div>
              </div>
            </div>

            {/* Quick Submit Shortcut */}
            <div className="pt-2">
              <button
                type="button"
                onClick={handleQuickSubmit}
                className="w-full py-2 px-4 rounded-xl bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white text-xs font-bold shadow-lg shadow-emerald-900/30 flex items-center justify-center gap-2 transition-all active:scale-95"
              >
                <span>⚡ দ্রুত অ্যাকাউন্ট খুলুন (Quick Register)</span>
              </button>
            </div>
          </div>
        )}

        {/* STEP 2: NID-Based Address (Present & Permanent) */}
        {step === 2 && (
          <div className="space-y-4 animate-fade-in max-h-[360px] overflow-y-auto pr-1">
            <div className="bg-slate-800/60 p-3 rounded-xl border border-slate-700/60 space-y-3">
              <h4 className="text-xs font-bold text-sky-400 flex items-center gap-1.5">
                <MapPin size={14} /> বর্তমান ঠিকানা (Present Address)
              </h4>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="text-[11px] text-slate-300 block mb-1">বিভাগ</label>
                  <select
                    value={formData.presentAddress.division}
                    onChange={e => handlePresentAddressChange('division', e.target.value)}
                    className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white focus:outline-none focus:border-sky-500"
                  >
                    {Object.keys(BD_DIVISIONS).map(div => (
                      <option key={div} value={div}>{div}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="text-[11px] text-slate-300 block mb-1">জেলা</label>
                  <select
                    value={formData.presentAddress.district}
                    onChange={e => handlePresentAddressChange('district', e.target.value)}
                    className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white focus:outline-none focus:border-sky-500"
                  >
                    {(BD_DIVISIONS[formData.presentAddress.division] || []).map(dist => (
                      <option key={dist} value={dist}>{dist}</option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <input
                  type="text"
                  placeholder="উপজেলা / থানা"
                  value={formData.presentAddress.upazila || ''}
                  onChange={e => handlePresentAddressChange('upazila', e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white placeholder-slate-500"
                />
                <input
                  type="text"
                  placeholder="পৌরসভা / ইউনিয়ন"
                  value={formData.presentAddress.unionCity || ''}
                  onChange={e => handlePresentAddressChange('unionCity', e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white placeholder-slate-500"
                />
              </div>
              <input
                type="text"
                placeholder="গ্রাম / ওয়ার্ড / বাসা নং / রোড"
                value={formData.presentAddress.details || ''}
                onChange={e => handlePresentAddressChange('details', e.target.value)}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white placeholder-slate-500"
              />
            </div>

            {/* Same address toggle */}
            <label className="flex items-center gap-2 cursor-pointer bg-slate-800/40 p-2.5 rounded-xl border border-slate-700/40">
              <input
                type="checkbox"
                checked={formData.sameAddress}
                onChange={e => {
                  const checked = e.target.checked;
                  setFormData(prev => ({
                    ...prev,
                    sameAddress: checked,
                    permanentAddress: checked ? prev.presentAddress : prev.permanentAddress
                  }));
                }}
                className="w-4 h-4 rounded text-sky-500 bg-slate-900 border-slate-700 focus:ring-sky-500"
              />
              <span className="text-xs text-slate-200">স্থায়ী ঠিকানা বর্তমান ঠিকানার মতোই</span>
            </label>

            {!formData.sameAddress && (
              <div className="bg-slate-800/60 p-3 rounded-xl border border-slate-700/60 space-y-3 animate-fade-in">
                <h4 className="text-xs font-bold text-sky-400 flex items-center gap-1.5">
                  <Building size={14} /> স্থায়ী ঠিকানা (Permanent Address)
                </h4>
                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label className="text-[11px] text-slate-300 block mb-1">বিভাগ</label>
                    <select
                      value={formData.permanentAddress.division}
                      onChange={e => handlePermanentAddressChange('division', e.target.value)}
                      className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white focus:outline-none focus:border-sky-500"
                    >
                      {Object.keys(BD_DIVISIONS).map(div => (
                        <option key={div} value={div}>{div}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="text-[11px] text-slate-300 block mb-1">জেলা</label>
                    <select
                      value={formData.permanentAddress.district}
                      onChange={e => handlePermanentAddressChange('district', e.target.value)}
                      className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white focus:outline-none focus:border-sky-500"
                    >
                      {(BD_DIVISIONS[formData.permanentAddress.division] || []).map(dist => (
                        <option key={dist} value={dist}>{dist}</option>
                      ))}
                    </select>
                  </div>
                </div>
                <input
                  type="text"
                  placeholder="বিস্তারিত স্থায়ী ঠিকানা"
                  value={formData.permanentAddress.details || ''}
                  onChange={e => handlePermanentAddressChange('details', e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 rounded-lg p-2 text-xs text-white placeholder-slate-500"
                />
              </div>
            )}
          </div>
        )}

        {/* STEP 3: Career, Education & Personal */}
        {step === 3 && (
          <div className="space-y-3 animate-fade-in max-h-[360px] overflow-y-auto pr-1">
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">পেশা / চাকরির বিবরণ (Job / Occupation)</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <Briefcase size={16} />
                </span>
                <input
                  type="text"
                  value={formData.jobInfo || ''}
                  onChange={e => handleInputChange('jobInfo', e.target.value)}
                  placeholder="যেমন: ফ্রিল্যান্স ডেভেলপার / ছাত্র"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-4 text-xs text-white placeholder-slate-500"
                />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">বিশ্ববিদ্যালয় / প্রতিষ্ঠান (University / Institute)</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400">
                  <GraduationCap size={16} />
                </span>
                <input
                  type="text"
                  value={formData.university || ''}
                  onChange={e => handleInputChange('university', e.target.value)}
                  placeholder="ঢাকা বিশ্ববিদ্যালয়"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-4 text-xs text-white placeholder-slate-500"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2">
              <div>
                ي<label className="text-xs font-semibold text-slate-300 block mb-1">কলেজ</label>
                <input
                  type="text"
                  value={formData.college || ''}
                  onChange={e => handleInputChange('college', e.target.value)}
                  placeholder="কলেজের নাম"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 px-3 text-xs text-white placeholder-slate-500"
                />
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">স্কুল</label>
                <input
                  type="text"
                  value={formData.school || ''}
                  onChange={e => handleInputChange('school', e.target.value)}
                  placeholder="স্কুলের নাম"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 px-3 text-xs text-white placeholder-slate-500"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-2">
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">জন্ম তারিখ</label>
                <div className="relative">
                  <span className="absolute inset-y-0 left-0 flex items-center pl-2.5 text-slate-400">
                    <Calendar size={14} />
                  </span>
                  <input
                    type="date"
                    value={formData.dob || ''}
                    onChange={e => handleInputChange('dob', e.target.value)}
                    className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-8 pr-2 text-[11px] text-white"
                  />
                </div>
              </div>
              <div>
                <label className="text-xs font-semibold text-slate-300 block mb-1">লিঙ্গ / জেন্ডার</label>
                <select
                  value={formData.gender || 'ছাত্র / শিক্ষার্থী'}
                  onChange={e => handleInputChange('gender', e.target.value)}
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 px-3 text-xs text-white"
                >
                  <option value="ছাত্র / শিক্ষার্থী">ছাত্র / শিক্ষার্থী</option>
                  <option value="পুরুষ">পুরুষ</option>
                  <option value="মহিলা">মহিলা</option>
                  <option value="অন্যান্য">অন্যান্য</option>
                </select>
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">আগ্রহের বিষয়সমূহ (Interests / Tags)</label>
              <input
                type="text"
                value={formData.interests || ''}
                onChange={e => handleInputChange('interests', e.target.value)}
                placeholder="কমা দিয়ে লিখুন (যেমন: টেক, গেমিং, পড়াশোনা)"
                className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 px-3 text-xs text-white placeholder-slate-500"
              />
            </div>
          </div>
        )}

        {/* STEP 4: Profile, Social & Terms */}
        {step === 4 && (
          <div className="space-y-3.5 animate-fade-in max-h-[360px] overflow-y-auto pr-1">
            {/* Avatar upload */}
            <div className="flex flex-col items-center justify-center">
              <div className="relative w-20 h-20 rounded-full border-2 border-sky-500/60 overflow-hidden bg-slate-800 flex items-center justify-center shadow-lg">
                {formData.avatarUrl ? (
                  <img src={formData.avatarUrl} alt="Avatar" className="w-full h-full object-cover" />
                ) : (
                  <span className="text-2xl font-bold text-sky-400 font-mono">
                    {formData.fullName ? formData.fullName.charAt(0) : 'KB'}
                  </span>
                )}
                <label className="absolute inset-0 bg-slate-950/60 flex flex-col items-center justify-center opacity-0 hover:opacity-100 transition-opacity cursor-pointer">
                  <Camera size={20} className="text-sky-400 mb-0.5" />
                  <span className="text-[9px] text-white">ছবি দিন</span>
                  <input type="file" accept="image/*" onChange={handleImageUpload} className="hidden" />
                </label>
              </div>
              <span className="text-[11px] text-slate-400 mt-1.5">প্রোফাইল ছবি (কম্প্রেসড ও অপ্টিমাইজড)</span>
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">সংক্ষিপ্ত বায়ো (Bio)</label>
              <textarea
                rows={2}
                value={formData.bio || ''}
                onChange={e => handleInputChange('bio', e.target.value)}
                placeholder="নিজের সম্পর্কে এক লাইনে কিছু লিখুন..."
                className="w-full bg-slate-800 border border-slate-700 rounded-xl p-2.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-sky-500"
              />
            </div>

            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">ব্যবহারকারীর ধরণ (Are You)</label>
              <select
                value={formData.userType}
                onChange={e => handleInputChange('userType', e.target.value)}
                className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 px-3 text-xs text-white"
              >
                <option value="ছাত্র / শিক্ষার্থী">ছাত্র / শিক্ষার্থী (Student)</option>
                <option value="স্কাউট / ফিল্ড ভেরিফায়ার">স্কাউট / ফিল্ড ভেরিফায়ার (Scout / Verifier)</option>
                <option value="চাকরিজীবী">চাকরিজীবী (Jobholder)</option>
                <option value="ফ্রিল্যান্সার">ফ্রিল্যান্সার (Freelancer)</option>
                <option value="ব্যবসায়ী">ব্যবসায়ী (Business Owner)</option>
                <option value="সাধারণ সদস্য">সাধারণ সদস্য (General Member)</option>
              </select>
            </div>

            {/* Referral code */}
            <div>
              <label className="text-xs font-semibold text-slate-300 block mb-1">রেফারেল কোড (বোনাসের জন্য)</label>
              <div className="relative">
                <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-sky-400">
                  <CardGiftcard size={16} />
                </span>
                <input
                  type="text"
                  value={formData.refCode || ''}
                  onChange={e => handleInputChange('refCode', e.target.value)}
                  placeholder="REFF50 (ঐচ্ছিক)"
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl py-2.5 pl-10 pr-4 text-xs font-mono text-white placeholder-slate-500"
                />
              </div>
            </div>

            {/* Contact sync checkbox */}
            <label className="flex items-start gap-2.5 cursor-pointer bg-slate-800/40 p-2.5 rounded-xl border border-slate-700/40">
              <input
                type="checkbox"
                checked={formData.contactSyncEnabled}
                onChange={e => handleInputChange('contactSyncEnabled', e.target.checked)}
                className="w-4 h-4 rounded text-sky-500 bg-slate-900 border-slate-700 mt-0.5"
              />
              <div className="text-xs text-slate-300 leading-tight">
                <span className="font-semibold text-white block">কন্টাক্ট সিঙ্ক ও ইনভাইট ইঞ্জিন</span>
                বন্ধুদের সাথে সহজে যুক্ত হতে ফোনবুক কন্টাক্ট ডিসকভারি এনাবল করুন।
              </div>
            </label>

            {/* Terms Agreement */}
            <label className="flex items-start gap-2.5 cursor-pointer pt-1">
              <input
                type="checkbox"
                checked={formData.acceptedTerms}
                onChange={e => handleInputChange('acceptedTerms', e.target.checked)}
                className="w-4 h-4 rounded text-sky-500 bg-slate-900 border-slate-700 mt-0.5"
              />
              <span className="text-[11px] text-slate-300 leading-tight">
                আমি ক্লিকবিডি-এর <span className="text-sky-400 underline">শর্তাবলী ও প্রাইভেসি পলিসি</span>-তে সম্মত আছি।
              </span>
            </label>
          </div>
        )}

        {/* Wizard Navigation Buttons */}
        <div className="flex items-center justify-between pt-3 border-t border-slate-800">
          {step > 1 ? (
            <button
              type="button"
              onClick={handlePrevStep}
              className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold flex items-center gap-1.5 transition-all"
            >
              <ArrowLeft size={14} /> পূর্ববর্তী
            </button>
          ) : (
            <button
              type="button"
              onClick={onSwitchToLogin}
              className="text-xs text-sky-400 hover:underline font-medium"
            >
              ইতিমধ্যে অ্যাকাউন্ট আছে? লগইন
            </button>
          )}

          {step < 4 ? (
            <button
              type="button"
              onClick={handleNextStep}
              className="px-5 py-2 rounded-xl bg-sky-500 hover:bg-sky-400 text-slate-950 text-xs font-bold shadow-lg shadow-sky-500/20 flex items-center gap-1.5 transition-all"
            >
              পরবর্তী <ArrowRight size={14} />
            </button>
          ) : (
            <button
              type="submit"
              className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-sky-500 to-indigo-600 hover:from-sky-400 hover:to-indigo-500 text-white text-xs font-bold shadow-lg shadow-sky-500/30 flex items-center gap-2 transition-all active:scale-95"
            >
              <ShieldCheck size={16} /> অ্যাকাউন্ট তৈরি করুন
            </button>
          )}
        </div>
      </form>
    </div>
  );
};
