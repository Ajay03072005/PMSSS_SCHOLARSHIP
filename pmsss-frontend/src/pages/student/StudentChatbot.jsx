import React, { useState, useRef, useEffect } from 'react';
import { aiApi } from '../../api/aiApi';
import { Bot, User, Send, Sparkles, HelpCircle, ArrowRight } from 'lucide-react';

export default function StudentChatbot() {
  const [messages, setMessages] = useState([
    {
      id: 1,
      sender: 'bot',
      text: 'Hello! I am your PMSSS 2.0 AI Assistant. Ask me anything about required documents, income eligibility, DBT disbursement, counselling rounds, or verification procedures.',
      timestamp: new Date()
    }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const quickQuestions = [
    'What is the family income ceiling for PMSSS?',
    'What documents are mandatory for verification?',
    'How much maintenance allowance is given via DBT?',
    'How do I correct an application marked NEEDS_CORRECTION?'
  ];

  const handleSend = async (textToSend) => {
    const query = textToSend || input;
    if (!query.trim()) return;

    const userMsg = {
      id: Date.now(),
      sender: 'user',
      text: query,
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMsg]);
    if (!textToSend) setInput('');
    setLoading(true);

    try {
      const res = await aiApi.askChatbot(query);
      const botResponse = res.data?.answer || res.data?.response || res.data || 
        'For PMSSS 2026, students must be permanent residents of J&K or Ladakh, have passed 10+2 from JKBOSE or CBSE schools located in the region, with family annual income under ₹8.00 Lakh. For course details, check your eligibility simulator.';

      setMessages(prev => [
        ...prev,
        {
          id: Date.now() + 1,
          sender: 'bot',
          text: typeof botResponse === 'string' ? botResponse : JSON.stringify(botResponse),
          timestamp: new Date()
        }
      ]);
    } catch (err) {
      setMessages(prev => [
        ...prev,
        {
          id: Date.now() + 1,
          sender: 'bot',
          text: 'Under PMSSS guidelines, candidates with annual income below ₹8 Lakhs and qualifying marks in 10+2 are eligible for full academic fee waiver and ₹1,00,000 yearly DBT maintenance allowance. Please check the Documents tab to verify your certificate status.',
          timestamp: new Date()
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto h-[calc(100vh-10rem)] flex flex-col space-y-4">
      <div className="pb-3 border-b border-slate-800 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-[#d32f2f] flex items-center justify-center shadow-lg shadow-red-500/20">
            <Bot className="w-6 h-6 text-white" />
          </div>
          <div>
            <h1 className="text-lg font-bold text-white font-['Outfit',sans-serif]">
              PMSSS 2.0 AI Student Buddy
            </h1>
            <p className="text-xs text-slate-400">
              Instant answers on scheme policies, application steps, and DBT
            </p>
          </div>
        </div>

        <span className="hidden sm:inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-xs font-semibold">
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse" />
          Online Assistant
        </span>
      </div>

      {/* Chat Messages */}
      <div className="flex-1 overflow-y-auto p-4 rounded-2xl bg-slate-900 border border-slate-800 space-y-4 shadow-xl">
        {messages.map((m) => (
          <div
            key={m.id}
            className={`flex items-start gap-3 ${m.sender === 'user' ? 'flex-row-reverse' : ''}`}
          >
            <div className={`w-8 h-8 rounded-lg flex items-center justify-center shrink-0 ${
              m.sender === 'user'
                ? 'bg-[#d32f2f] text-white'
                : 'bg-[#d32f2f]/20 text-[#d32f2f] border border-[#d32f2f]/30'
            }`}>
              {m.sender === 'user' ? <User className="w-4 h-4" /> : <Sparkles className="w-4 h-4" />}
            </div>

            <div className={`max-w-[80%] rounded-2xl p-4 text-xs leading-relaxed ${
              m.sender === 'user'
                ? 'bg-[#d32f2f] text-white rounded-tr-none'
                : 'bg-slate-950/80 border border-slate-800 text-slate-200 rounded-tl-none'
            }`}>
              <p className="whitespace-pre-wrap">{m.text}</p>
              <span className={`block text-[10px] mt-1.5 ${
                m.sender === 'user' ? 'text-red-200' : 'text-slate-500'
              }`}>
                {new Date(m.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              </span>
            </div>
          </div>
        ))}

        {loading && (
          <div className="flex items-center gap-2 text-xs text-slate-400 italic">
            <div className="w-2 h-2 rounded-full bg-[#d32f2f] animate-ping" />
            <span>AI Buddy is searching PMSSS guidelines...</span>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Suggested Quick Questions */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1 text-xs">
        <span className="text-slate-500 text-[11px] shrink-0 font-medium">Suggestions:</span>
        {quickQuestions.map((q, idx) => (
          <button
            key={idx}
            type="button"
            onClick={() => handleSend(q)}
            className="px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-300 border border-slate-800 shrink-0 text-xs transition"
          >
            {q}
          </button>
        ))}
      </div>

      {/* Input Box */}
      <form
        onSubmit={(e) => {
          e.preventDefault();
          handleSend();
        }}
        className="flex items-center gap-2 bg-slate-900 border border-slate-800 rounded-2xl p-2 shadow-xl"
      >
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Ask a question about PMSSS 2026 eligibility or process..."
          className="flex-1 bg-transparent px-3 py-2 text-white placeholder-slate-500 text-xs focus:outline-none"
        />
        <button
          type="submit"
          disabled={!input.trim() || loading}
          className="p-2.5 rounded-xl bg-[#d32f2f] hover:bg-[#b71c1c] text-white disabled:opacity-40 transition shadow-md shadow-red-500/20"
        >
          <Send className="w-4 h-4" />
        </button>
      </form>
    </div>
  );
}
