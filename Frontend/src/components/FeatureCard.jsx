import React from "react";

const FeatureCard = React.memo(({ feature }) => {
  const Icon = feature.icon;

  return (
    <div className="bg-white/5 backdrop-blur border border-white/10 rounded-xl p-6 hover:border-orange-500/30 transition-all duration-300 group">
      <div className="space-y-4">
        <div className="w-12 h-12 bg-orange-500/10 rounded-xl flex items-center justify-center group-hover:bg-orange-500/20 transition-colors">
          <Icon size={24} className="text-orange-400" />
        </div>
        <h3 className="text-xl font-semibold text-white font-mono">
          {feature.title}
        </h3>
        <p className="text-gray-500">{feature.description}</p>
      </div>
    </div>
  );
});

export default FeatureCard;
