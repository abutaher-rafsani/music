import React, { createContext, useContext, useState, useCallback } from 'react';
import { Loader2 } from 'lucide-react';

interface LoadingContextType {
  isLoading: boolean;
  loadingText: string;
  showLoading: (text?: string) => void;
  hideLoading: () => void;
}

const LoadingContext = createContext<LoadingContextType | undefined>(undefined);

export const LoadingProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [loadingText, setLoadingText] = useState<string>('দয়া করে অপেক্ষা করুন...');

  const showLoading = useCallback((text: string = 'দয়া করে অপেক্ষা করুন...') => {
    setLoadingText(text);
    setIsLoading(true);
  }, []);

  const hideLoading = useCallback(() => {
    setIsLoading(false);
  }, []);

  return (
    <LoadingContext.Provider value={{ isLoading, loadingText, showLoading, hideLoading }}>
      {children}
      {isLoading && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-md z-50 flex flex-col items-center justify-center p-4 animate-fade-in">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-2xl flex flex-col items-center max-w-xs w-full">
            <div className="relative mb-4">
              <div className="w-12 h-12 rounded-full border-4 border-sky-500/20 border-t-sky-500 animate-spin flex items-center justify-center"></div>
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="w-4 h-4 bg-indigo-500 rounded-full animate-pulse"></div>
              </div>
            </div>
            <p className="text-sm font-bold text-white text-center mb-1">প্রসেসিং হচ্ছে...</p>
            <p className="text-xs text-sky-400 text-center">{loadingText}</p>
          </div>
        </div>
      )}
    </LoadingContext.Provider>
  );
};

export const useLoading = () => {
  const context = useContext(LoadingContext);
  if (!context) {
    throw new Error('useLoading must be used within a LoadingProvider');
  }
  return context;
};
