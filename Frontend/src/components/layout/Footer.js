import { Brain, Github, Linkedin, Mail, Twitter } from 'lucide-react';

const Footer = () => {
  const currentYear = new Date().getFullYear();

  const footerSections = [
    {
      title: 'Product',
      links: [
        { name: 'Features', href: '/#features' },
        { name: 'Pricing', href: '/#pricing' },
        { name: 'Use Cases', href: '/#use-cases' },
      ],
    },
    {
      title: 'Company',
      links: [
        { name: 'About', href: '/#about' },
        { name: 'Blog', href: '/#blog' },
        { name: 'Careers', href: '/#careers' },
      ],
    },
    {
      title: 'Resources',
      links: [
        { name: 'Documentation', href: '/#docs' },
        { name: 'Help Center', href: '/#help' },
        { name: 'Community', href: '/#community' },
      ],
    },
    {
      title: 'Legal',
      links: [
        { name: 'Privacy Policy', href: '/#privacy' },
        { name: 'Terms of Service', href: '/#terms' },
        { name: 'Cookie Policy', href: '/#cookies' },
      ],
    },
  ];

  const socialLinks = [
    { icon: Github, href: '#', label: 'GitHub' },
    { icon: Twitter, href: '#', label: 'Twitter' },
    { icon: Linkedin, href: '#', label: 'LinkedIn' },
    { icon: Mail, href: 'mailto:contact@quizora.ai', label: 'Email' },
  ];

  return (
    <footer className="bg-[#0a0a0a] text-white mt-auto border-t border-white/5" data-scroll-section>
      <div className="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="space-y-8">
          {/* Main Footer Content */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-8">
            {/* Brand Section */}
            <div className="lg:col-span-2">
              <div className="flex items-center space-x-2 mb-4">
                <Brain size={32} className="text-orange-400" />
                <h3 className="text-xl font-bold bg-gradient-to-r from-orange-400 via-orange-500 to-red-500 bg-clip-text text-transparent">Quizora AI</h3>
              </div>
              <p className="text-gray-400 mb-4 max-w-xs">
                Empowering learners worldwide with AI-powered quizzes and interviews. 
                Transform your learning experience with personalized content generation.
              </p>
              <div className="flex space-x-4">
                {socialLinks.map((social) => {
                  const Icon = social.icon;
                  return (
                    <a
                      key={social.label}
                      href={social.href}
                      aria-label={social.label}
                      className="text-gray-400 hover:text-orange-400 transition-colors p-2 rounded-lg hover:bg-white/5"
                    >
                      <Icon size={20} />
                    </a>
                  );
                })}
              </div>
            </div>

            {/* Links Sections */}
            <div className="lg:col-span-3 grid grid-cols-2 md:grid-cols-3 gap-8">
              {footerSections.map((section) => (
                <div key={section.title}>
                  <h4 className="font-semibold mb-4 text-white font-mono">{section.title}</h4>
                  <ul className="space-y-2">
                    {section.links.map((link) => (
                      <li key={link.name}>
                        <a
                          href={link.href}
                          className="text-gray-500 hover:text-orange-400 transition-colors"
                        >
                          {link.name}
                        </a>
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
            </div>
          </div>

          <hr className="border-white/10" />

          {/* Bottom Section */}
          <div className="flex flex-col md:flex-row items-center justify-between space-y-4 md:space-y-0 pt-8">
            <p className="text-gray-500 text-center md:text-left">
              © {currentYear} Quizora AI. All rights reserved.
            </p>
            <div className="flex space-x-6 text-gray-500 text-center md:text-right">
              <a href="/#privacy" className="hover:text-orange-400 transition-colors">
                Privacy
              </a>
              <a href="/#terms" className="hover:text-orange-400 transition-colors">
                Terms
              </a>
              <a href="/#cookies" className="hover:text-orange-400 transition-colors">
                Cookies
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
